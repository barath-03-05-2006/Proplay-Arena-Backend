package com.proplay.arena.service;

import com.proplay.arena.dto.request.CartItemRequest;
import com.proplay.arena.dto.response.CartResponse;
import com.proplay.arena.entity.Cart;
import com.proplay.arena.entity.CartItem;
import com.proplay.arena.entity.User;
import com.proplay.arena.exception.ResourceNotFoundException;
import com.proplay.arena.repository.CartRepository;
import com.proplay.arena.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    private Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        });
    }

    public CartResponse getCart(String email) {
        return CartResponse.from(getOrCreateCart(email));
    }

    @Transactional
    public CartResponse addItem(String email, CartItemRequest request) {
        Cart cart = getOrCreateCart(email);
        CartItem item = CartItem.builder()
                .cart(cart).type(request.getType()).referenceId(request.getReferenceId())
                .name(request.getName()).gameName(request.getGameName())
                .slotTime(request.getSlotTime()).duration(request.getDuration())
                .price(request.getPrice()).build();
        cart.getItems().add(item);
        return CartResponse.from(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(String email, Long itemId) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        return CartResponse.from(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public Map<String, Integer> getCount(String email) {
        return Map.of("count", getOrCreateCart(email).getItems().size());
    }
}
