package com.proplay.arena.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(nullable = false)
    private String name;

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "slot_time")
    private LocalDateTime slotTime;

    private Integer duration;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() { addedAt = LocalDateTime.now(); }

    public enum ItemType { PLAYER_BOOKING, TOURNAMENT_ENTRY }

    public CartItem() {}

    public Long getId() { return id; }
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    public ItemType getType() { return type; }
    public void setType(ItemType type) { this.type = type; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }
    public LocalDateTime getSlotTime() { return slotTime; }
    public void setSlotTime(LocalDateTime slotTime) { this.slotTime = slotTime; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final CartItem i = new CartItem();
        public Builder cart(Cart cart) { i.cart = cart; return this; }
        public Builder type(ItemType type) { i.type = type; return this; }
        public Builder referenceId(Long referenceId) { i.referenceId = referenceId; return this; }
        public Builder name(String name) { i.name = name; return this; }
        public Builder gameName(String gameName) { i.gameName = gameName; return this; }
        public Builder slotTime(LocalDateTime slotTime) { i.slotTime = slotTime; return this; }
        public Builder duration(Integer duration) { i.duration = duration; return this; }
        public Builder price(BigDecimal price) { i.price = price; return this; }
        public CartItem build() { return i; }
    }
}
