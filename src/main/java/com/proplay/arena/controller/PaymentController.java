package com.proplay.arena.controller;

import com.proplay.arena.dto.request.PaymentVerifyRequest;
import com.proplay.arena.dto.response.ApiResponse;
import com.proplay.arena.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentService.createOrder(userDetails.getUsername()));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyPayment(
            @Valid @RequestBody PaymentVerifyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentService.verifyPayment(userDetails.getUsername(), request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentService.getHistory(userDetails.getUsername()));
    }
}
