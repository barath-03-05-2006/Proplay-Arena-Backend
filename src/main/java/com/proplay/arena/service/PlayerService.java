package com.proplay.arena.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proplay.arena.dto.request.ProPlayerRequest;
import com.proplay.arena.dto.response.PlayerResponse;
import com.proplay.arena.entity.Booking;
import com.proplay.arena.entity.ProPlayer;
import com.proplay.arena.entity.User;
import com.proplay.arena.exception.BadRequestException;
import com.proplay.arena.exception.ResourceNotFoundException;
import com.proplay.arena.repository.BookingRepository;
import com.proplay.arena.repository.ProPlayerRepository;
import com.proplay.arena.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final ProPlayerRepository proPlayerRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlayerService(ProPlayerRepository proPlayerRepository,
                         UserRepository userRepository,
                         BookingRepository bookingRepository) {
        this.proPlayerRepository = proPlayerRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<PlayerResponse> getAll(String game, Boolean available) {
        List<ProPlayer> players;
        if (game != null || available != null) {
            players = proPlayerRepository.findApprovedByFilters(game, available);
        } else {
            players = proPlayerRepository.findAll().stream()
                    .filter(p -> p.getApprovalStatus() == ProPlayer.ApprovalStatus.APPROVED)
                    .collect(Collectors.toList());
        }
        return players.stream().map(PlayerResponse::from).collect(Collectors.toList());
    }

    public PlayerResponse getById(Long id) {
        return PlayerResponse.from(proPlayerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", id)));
    }

    public PlayerResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return PlayerResponse.from(proPlayerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Pro player profile not found")));
    }

    public PlayerResponse createProfile(String email, ProPlayerRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != User.Role.PRO_PLAYER)
            throw new BadRequestException("Only PRO_PLAYER accounts can create player profiles");
        if (proPlayerRepository.findByUser(user).isPresent())
            throw new BadRequestException("You already have a pro player profile");

        ProPlayer player = ProPlayer.builder()
                .user(user).game(request.getGame()).rank(request.getRank())
                .pricePerHour(request.getPricePerHour()).bio(request.getBio())
                .available(request.isAvailable())
                .winRate(request.getWinRate())
                .responseTime(request.getResponseTime() != null ? request.getResponseTime() : "Fast")
                .availability(request.getAvailability())
                .approvalStatus(ProPlayer.ApprovalStatus.PENDING).build();
        return PlayerResponse.from(proPlayerRepository.save(player));
    }

    public PlayerResponse updateProfile(String email, ProPlayerRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ProPlayer player = proPlayerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Pro player profile not found"));

        player.setGame(request.getGame());
        player.setRank(request.getRank());
        player.setPricePerHour(request.getPricePerHour());
        player.setBio(request.getBio());
        player.setAvailable(request.isAvailable());
        if (request.getWinRate() != null) player.setWinRate(request.getWinRate());
        if (request.getResponseTime() != null) player.setResponseTime(request.getResponseTime());
        if (request.getAvailability() != null) player.setAvailability(request.getAvailability());
        return PlayerResponse.from(proPlayerRepository.save(player));
    }

    public List<PlayerResponse> getTopPlayers() {
        return proPlayerRepository.findTopPlayers().stream()
                .limit(10).map(PlayerResponse::from).collect(Collectors.toList());
    }

    /**
     * Returns available slots for a player on a given date.
     * 1. Get player's set availability for that day of week
     * 2. Remove slots already booked
     * 3. If player set no availability → return all slots 9AM-10PM
     */
    public List<String> getAvailableSlots(Long playerId, String date) {
        ProPlayer player = proPlayerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player", playerId));

        LocalDate localDate = LocalDate.parse(date);
        // Get day name e.g. "Monday", "Tuesday"
        String dayName = localDate.getDayOfWeek()
                .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);

        // Step 1: Get player's set availability for this day
        List<String> playerSlots = getPlayerSlotsForDay(player, dayName);

        // Step 2: Get already booked slots
        LocalDateTime dayStart = localDate.atStartOfDay();
        LocalDateTime dayEnd = localDate.plusDays(1).atStartOfDay();
        List<Booking> existingBookings = bookingRepository
                .findByProPlayerIdAndDate(playerId, dayStart, dayEnd);

        Set<Integer> blockedHours = new HashSet<>();
        for (Booking b : existingBookings) {
            if (b.getStatus() == Booking.BookingStatus.CANCELLED) continue;
            int startHour = b.getSlotTime().getHour();
            int dur = b.getDuration() != null ? b.getDuration() : 1;
            for (int i = 0; i < dur; i++) {
                blockedHours.add(startHour + i);
            }
        }

        // Step 3: Filter out booked hours from player's available slots
        return playerSlots.stream()
                .filter(slot -> {
                    int hour = Integer.parseInt(slot.split(":")[0]);
                    return !blockedHours.contains(hour);
                })
                .collect(Collectors.toList());
    }

    /**
     * Parse player's JSON availability and return slots for a specific day.
     * Falls back to all slots 9AM-10PM if no availability set.
     */
    private List<String> getPlayerSlotsForDay(ProPlayer player, String dayName) {
        if (player.getAvailability() == null || player.getAvailability().isBlank()) {
            return generateAllSlots(); // fallback
        }
        try {
            Map<String, List<String>> availability = objectMapper.readValue(
                    player.getAvailability(),
                    new TypeReference<Map<String, List<String>>>() {});
            List<String> slots = availability.get(dayName);
            if (slots == null || slots.isEmpty()) return List.of(); // not available this day
            return slots;
        } catch (Exception e) {
            return generateAllSlots(); // fallback on parse error
        }
    }

    private List<String> generateAllSlots() {
        List<String> slots = new ArrayList<>();
        for (int hour = 9; hour <= 22; hour++) {
            slots.add(String.format("%02d:00", hour));
        }
        return slots;
    }
}
