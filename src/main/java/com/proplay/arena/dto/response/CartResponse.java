package com.proplay.arena.dto.response;

import com.proplay.arena.entity.Cart;
import com.proplay.arena.entity.CartItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CartResponse {

    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal total;

    public CartResponse() {}

    public static CartResponse from(Cart cart) {
        CartResponse r = new CartResponse();
        r.id = cart.getId();
        r.items = cart.getItems().stream().map(i -> {
            CartItemResponse cr = new CartItemResponse();
            cr.id = i.getId();
            cr.type = i.getType();
            cr.referenceId = i.getReferenceId();
            cr.name = i.getName();
            cr.gameName = i.getGameName();
            cr.slotTime = i.getSlotTime();
            cr.duration = i.getDuration();
            cr.price = i.getPrice();
            return cr;
        }).collect(Collectors.toList());
        r.total = cart.getTotal();
        return r;
    }

    public Long getId() { return id; }
    public List<CartItemResponse> getItems() { return items; }
    public BigDecimal getTotal() { return total; }

    public static class CartItemResponse {
        private Long id;
        private CartItem.ItemType type;
        private Long referenceId;
        private String name;
        private String gameName;
        private LocalDateTime slotTime;
        private Integer duration;
        private BigDecimal price;

        public CartItemResponse() {}

        public Long getId() { return id; }
        public CartItem.ItemType getType() { return type; }
        public Long getReferenceId() { return referenceId; }
        public String getName() { return name; }
        public String getGameName() { return gameName; }
        public LocalDateTime getSlotTime() { return slotTime; }
        public Integer getDuration() { return duration; }
        public BigDecimal getPrice() { return price; }
    }
}
