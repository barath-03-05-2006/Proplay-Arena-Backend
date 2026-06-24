package com.proplay.arena.controller;

import com.proplay.arena.dto.request.CartItemRequest;
import com.proplay.arena.dto.response.ApiResponse;
import com.proplay.arena.dto.response.CartResponse;
import com.proplay.arena.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.addItem(userDetails.getUsername(), request));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.removeItem(userDetails.getUsername(), itemId));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared"));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCount(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCount(userDetails.getUsername()));
    }
}
