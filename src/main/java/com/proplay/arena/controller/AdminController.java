package com.proplay.arena.controller;

import com.proplay.arena.dto.request.TournamentRequest;
import com.proplay.arena.dto.response.*;
import com.proplay.arena.entity.User;
import com.proplay.arena.exception.ResourceNotFoundException;
import com.proplay.arena.repository.UserRepository;
import com.proplay.arena.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService, UserRepository userRepository) {
        this.adminService = adminService;
        this.userRepository = userRepository;
    }

    // Dashboard
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // Players
    @GetMapping("/players")
    public ResponseEntity<List<PlayerResponse>> getAllPlayers(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getAllPlayers(status));
    }

    @GetMapping("/players/pending")
    public ResponseEntity<List<PlayerResponse>> getPendingPlayers() {
        return ResponseEntity.ok(adminService.getPendingPlayers());
    }

    @PutMapping("/players/{id}/approve")
    public ResponseEntity<PlayerResponse> approvePlayer(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approvePlayer(id));
    }

    @PutMapping("/players/{id}/reject")
    public ResponseEntity<PlayerResponse> rejectPlayer(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.rejectPlayer(id));
    }

    // Tournaments
    @GetMapping("/tournaments")
    public ResponseEntity<List<TournamentResponse>> getAllTournaments(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getAllTournaments(status));
    }

    @PostMapping("/tournaments")
    public ResponseEntity<TournamentResponse> createTournament(
            @Valid @RequestBody TournamentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(adminService.createTournament(request, admin.getId()));
    }

    @PutMapping("/tournaments/{id}")
    public ResponseEntity<TournamentResponse> updateTournament(
            @PathVariable Long id,
            @Valid @RequestBody TournamentRequest request) {
        return ResponseEntity.ok(adminService.updateTournament(id, request));
    }

    @DeleteMapping("/tournaments/{id}")
    public ResponseEntity<ApiResponse> deleteTournament(@PathVariable Long id) {
        adminService.deleteTournament(id);
        return ResponseEntity.ok(ApiResponse.ok("Tournament deleted"));
    }

    @PostMapping("/tournaments/{id}/winners")
    public ResponseEntity<TournamentResponse> declareWinner(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.declareWinner(id, body.get("winnerTeam")));
    }

    // Users
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers(@RequestParam(required = false) String role) {
        return ResponseEntity.ok(adminService.getAllUsers(role));
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<ApiResponse> banUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.banUser(id));
    }

    @PutMapping("/users/{id}/unban")
    public ResponseEntity<ApiResponse> unbanUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.unbanUser(id));
    }

    // Payments
    @GetMapping("/payments")
    public ResponseEntity<List<Map<String, Object>>> getAllPayments(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getAllPayments(status));
    }
}
