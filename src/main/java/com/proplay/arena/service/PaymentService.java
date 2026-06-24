package com.proplay.arena.service;

import com.proplay.arena.dto.request.PaymentVerifyRequest;
import com.proplay.arena.dto.response.ApiResponse;
import com.proplay.arena.entity.*;
import com.proplay.arena.exception.BadRequestException;
import com.proplay.arena.exception.ResourceNotFoundException;
import com.proplay.arena.repository.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ProPlayerRepository proPlayerRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentRegistrationRepository registrationRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    public PaymentService(PaymentRepository paymentRepository,
                          CartRepository cartRepository,
                          UserRepository userRepository,
                          BookingRepository bookingRepository,
                          ProPlayerRepository proPlayerRepository,
                          TournamentRepository tournamentRepository,
                          TournamentRegistrationRepository registrationRepository) {
        this.paymentRepository = paymentRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.proPlayerRepository = proPlayerRepository;
        this.tournamentRepository = tournamentRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional
    public Map<String, Object> createOrder(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));
        if (cart.getItems().isEmpty())
            throw new BadRequestException("Cart is empty");

        // ✅ Validate slot availability BEFORE creating order
        for (CartItem item : cart.getItems()) {
            if (item.getType() == CartItem.ItemType.PLAYER_BOOKING) {
                validateSlotAvailability(item);
            }
        }

        BigDecimal subtotal = cart.getTotal();
        BigDecimal gst = subtotal.multiply(BigDecimal.valueOf(0.18));
        BigDecimal total = subtotal.add(gst);

        long amountInPaise = total.multiply(BigDecimal.valueOf(100)).longValue();

        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "rcpt_" + user.getId() + "_" + System.currentTimeMillis());

            Order razorpayOrder = razorpay.orders.create(orderRequest);
            String razorpayOrderId = razorpayOrder.get("id");

            Payment payment = Payment.builder()
                    .user(user).razorpayOrderId(razorpayOrderId)
                    .amount(total).currency("INR")
                    .status(Payment.PaymentStatus.PENDING).build();
            paymentRepository.save(payment);

            return Map.of(
                    "orderId", razorpayOrderId,
                    "amount", amountInPaise,
                    "currency", "INR",
                    "keyId", razorpayKeyId
            );
        } catch (RazorpayException e) {
            throw new BadRequestException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    // ✅ Validate that the slot is not already taken by another booking
    private void validateSlotAvailability(CartItem item) {
        if (item.getSlotTime() == null) return;

        int duration = item.getDuration() != null ? item.getDuration() : 1;
        LocalDateTime slotStart = item.getSlotTime();
        LocalDateTime slotEnd = slotStart.plusHours(duration);

        boolean conflict = bookingRepository.isSlotConflicting(
                item.getReferenceId(), slotStart, slotEnd);

        if (conflict) {
            throw new BadRequestException(
                "The selected time slot " + slotStart.getHour() + ":00 is already booked. " +
                "Please go back and select a different time slot.");
        }
    }

    @Transactional
    public ApiResponse verifyPayment(String email, PaymentVerifyRequest request) {
        String payload = request.getRazorpay_order_id() + "|" + request.getRazorpay_payment_id();
        if (!verifyHmacSignature(payload, request.getRazorpay_signature(), razorpayKeySecret))
            throw new BadRequestException("Payment verification failed: invalid signature");

        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpay_order_id())
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));
        payment.setRazorpayPaymentId(request.getRazorpay_payment_id());
        payment.setRazorpaySignature(request.getRazorpay_signature());
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        cartRepository.findByUser(user).ifPresent(cart -> {
            for (CartItem item : cart.getItems()) {
                if (item.getType() == CartItem.ItemType.PLAYER_BOOKING) {
                    createPlayerBooking(user, item, payment);
                } else if (item.getType() == CartItem.ItemType.TOURNAMENT_ENTRY) {
                    createTournamentRegistration(user, item);
                }
            }
            cart.getItems().clear();
            cartRepository.save(cart);
        });

        return ApiResponse.ok("Payment verified successfully");
    }

    private void createPlayerBooking(User user, CartItem item, Payment payment) {
        proPlayerRepository.findById(item.getReferenceId()).ifPresent(proPlayer -> {
            proPlayer.setTotalSessions(proPlayer.getTotalSessions() + 1);
            proPlayerRepository.save(proPlayer);

            Booking booking = Booking.builder()
                    .user(user).proPlayer(proPlayer).payment(payment)
                    .slotTime(item.getSlotTime() != null ? item.getSlotTime() : LocalDateTime.now())
                    .duration(item.getDuration() != null ? item.getDuration() : 1)
                    .amount(item.getPrice())
                    .status(Booking.BookingStatus.CONFIRMED).build();
            bookingRepository.save(booking);
        });
    }

    private void createTournamentRegistration(User user, CartItem item) {
        tournamentRepository.findById(item.getReferenceId()).ifPresent(tournament -> {
            if (!registrationRepository.existsByTournamentIdAndUserId(tournament.getId(), user.getId())) {
                TournamentRegistration reg = TournamentRegistration.builder()
                        .tournament(tournament).user(user)
                        .teamName(user.getUsername() + "'s Team").build();
                registrationRepository.save(reg);
                tournament.setRegisteredTeams(tournament.getRegisteredTeams() + 1);
                tournamentRepository.save(tournament);
            }
        });
    }

    public List<Map<String, Object>> getHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return paymentRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(p -> Map.of(
                        "id", (Object) p.getId(),
                        "razorpayOrderId", p.getRazorpayOrderId() != null ? p.getRazorpayOrderId() : "",
                        "razorpayPaymentId", p.getRazorpayPaymentId() != null ? p.getRazorpayPaymentId() : "",
                        "amount", p.getAmount(), "currency", p.getCurrency(),
                        "status", p.getStatus().name(),
                        "createdAt", p.getCreatedAt().toString()))
                .collect(Collectors.toList());
    }

    private boolean verifyHmacSignature(String payload, String signature, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String computed = HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
            return computed.equals(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error verifying payment signature", e);
        }
    }
}
