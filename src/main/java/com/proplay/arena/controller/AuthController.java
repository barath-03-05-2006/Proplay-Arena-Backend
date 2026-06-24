package com.proplay.arena.controller;

import com.proplay.arena.dto.request.ForgotPasswordRequest;
import com.proplay.arena.dto.request.LoginRequest;
import com.proplay.arena.dto.request.RegisterRequest;
import com.proplay.arena.dto.request.ResetPasswordRequest;
import com.proplay.arena.dto.response.ApiResponse;
import com.proplay.arena.dto.response.AuthResponse;
import com.proplay.arena.service.AuthService;
import com.proplay.arena.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getMe(userDetails.getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }

    // ── Forgot Password ──────────────────────────────────
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.forgotPassword(request));
    }

    // ── Validate token (frontend checks if link is still valid) ──
    @GetMapping("/reset-password/validate")
    public ResponseEntity<ApiResponse> validateResetToken(@RequestParam String token) {
        return ResponseEntity.ok(passwordResetService.validateToken(token));
    }

    // ── Reset Password ───────────────────────────────────
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.resetPassword(request));
    }
}
