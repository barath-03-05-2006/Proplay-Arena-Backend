package com.proplay.arena.controller;

import com.proplay.arena.dto.response.TournamentResponse;
import com.proplay.arena.dto.response.ApiResponse;
import com.proplay.arena.service.TournamentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> getAll(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(tournamentService.getAll(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getById(id));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TournamentResponse>> getMyTournaments(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tournamentService.getMyTournaments(userDetails.getUsername()));
    }

    @PostMapping("/{id}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TournamentResponse> join(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tournamentService.join(id, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}/leave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> leave(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        tournamentService.leave(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Left tournament successfully"));
    }

    @GetMapping("/{id}/bracket")
    public ResponseEntity<List<Object>> getBracket(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getBracket(id));
    }

    @GetMapping("/{id}/leaderboard")
    public ResponseEntity<List<Object>> getLeaderboard(@PathVariable Long id) {
        return ResponseEntity.ok(List.of());
    }
}
