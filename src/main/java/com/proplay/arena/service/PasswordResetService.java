package com.proplay.arena.service;

import com.proplay.arena.dto.request.ForgotPasswordRequest;
import com.proplay.arena.dto.request.ResetPasswordRequest;
import com.proplay.arena.dto.response.ApiResponse;
import com.proplay.arena.entity.PasswordResetToken;
import com.proplay.arena.entity.User;
import com.proplay.arena.exception.BadRequestException;
import com.proplay.arena.repository.PasswordResetTokenRepository;
import com.proplay.arena.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        // Always return success to prevent email enumeration
        if (userOpt.isEmpty()) {
            return ApiResponse.ok("If this email is registered, a reset link has been sent.");
        }

        User user = userOpt.get();

        // Delete existing tokens for this user
        tokenRepository.deleteAllByUser(user);

        // Generate secure token
        String token = UUID.randomUUID().toString().replace("-", "")
                     + UUID.randomUUID().toString().replace("-", "");

        // Save token - expires in 15 minutes
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(resetToken);

        // Send email — throw error if it fails so frontend knows
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), token);
        } catch (Exception e) {
            // Delete token if email failed
            tokenRepository.delete(resetToken);
            throw new BadRequestException(
                "Failed to send email. Please check your email address and try again. Error: " + e.getMessage());
        }

        return ApiResponse.ok("Password reset link sent to " + maskEmail(user.getEmail()));
    }

    @Transactional
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException(
                    "Invalid or expired reset link. Please request a new one."));

        if (resetToken.isUsed()) {
            throw new BadRequestException("This reset link has already been used.");
        }

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new BadRequestException("This reset link has expired (15 min). Please request a new one.");
        }

        if (request.getNewPassword().length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters.");
        }

        // Update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return ApiResponse.ok("Password reset successfully! You can now log in with your new password.");
    }

    public ApiResponse validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid reset link."));

        if (resetToken.isUsed()) {
            throw new BadRequestException("This reset link has already been used.");
        }
        if (resetToken.isExpired()) {
            throw new BadRequestException("This reset link has expired. Please request a new one.");
        }

        return ApiResponse.ok("Token is valid");
    }

    // Masks email for privacy: bar****@gmail.com
    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 3) return email;
        return email.substring(0, 3) + "****" + email.substring(at);
    }
}
