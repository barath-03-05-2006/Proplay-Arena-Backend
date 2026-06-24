package com.proplay.arena.controller;

import com.proplay.arena.entity.Booking;
import com.proplay.arena.entity.ProPlayer;
import com.proplay.arena.entity.User;
import com.proplay.arena.exception.ResourceNotFoundException;
import com.proplay.arena.repository.BookingRepository;
import com.proplay.arena.repository.ProPlayerRepository;
import com.proplay.arena.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ProPlayerRepository proPlayerRepository;

    public BookingController(BookingRepository bookingRepository,
                             UserRepository userRepository,
                             ProPlayerRepository proPlayerRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.proPlayerRepository = proPlayerRepository;
    }

    // Get bookings for logged-in user (user dashboard)
    @GetMapping("/my")
    public ResponseEntity<List<Map<String, Object>>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Booking> bookings = bookingRepository.findByUserOrderByCreatedAtDesc(user);
        return ResponseEntity.ok(toMapList(bookings));
    }

    // Get bookings received by a pro player
    @GetMapping("/received")
    public ResponseEntity<List<Map<String, Object>>> getReceivedBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ProPlayer proPlayer = proPlayerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Pro player profile not found"));
        List<Booking> bookings = bookingRepository.findByProPlayerOrderByCreatedAtDesc(proPlayer);
        return ResponseEntity.ok(toMapList(bookings));
    }

    private List<Map<String, Object>> toMapList(List<Booking> bookings) {
        return bookings.stream().map(b -> Map.of(
                "id", (Object) b.getId(),
                "proPlayerName", b.getProPlayer().getUser().getUsername(),
                "game", b.getProPlayer().getGame(),
                "bookedByName", b.getUser().getUsername(),
                "slotTime", b.getSlotTime().toString(),
                "duration", b.getDuration(),
                "amount", b.getAmount(),
                "status", b.getStatus().name(),
                "createdAt", b.getCreatedAt().toString()
        )).collect(Collectors.toList());
    }
}
