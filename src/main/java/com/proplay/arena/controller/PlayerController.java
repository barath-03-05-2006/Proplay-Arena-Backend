package com.proplay.arena.controller;

import com.proplay.arena.dto.request.ProPlayerRequest;
import com.proplay.arena.dto.response.PlayerResponse;
import com.proplay.arena.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAll(
            @RequestParam(required = false) String game,
            @RequestParam(required = false) Boolean available) {
        return ResponseEntity.ok(playerService.getAll(game, available));
    }

    @GetMapping("/top")
    public ResponseEntity<List<PlayerResponse>> getTop() {
        return ResponseEntity.ok(playerService.getTopPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getById(id));
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<List<String>> getSlots(
            @PathVariable Long id,
            @RequestParam String date) {
        return ResponseEntity.ok(playerService.getAvailableSlots(id, date));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PRO_PLAYER')")
    public ResponseEntity<PlayerResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(playerService.getMyProfile(userDetails.getUsername()));
    }

    @PostMapping
    @PreAuthorize("hasRole('PRO_PLAYER')")
    public ResponseEntity<PlayerResponse> createProfile(
            @Valid @RequestBody ProPlayerRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(playerService.createProfile(userDetails.getUsername(), request));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('PRO_PLAYER')")
    public ResponseEntity<PlayerResponse> updateProfile(
            @Valid @RequestBody ProPlayerRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(playerService.updateProfile(userDetails.getUsername(), request));
    }
}
