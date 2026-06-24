package com.proplay.arena.dto.request;

import com.proplay.arena.entity.CartItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItemRequest {

    @NotNull
    private CartItem.ItemType type;

    @NotNull
    private Long referenceId;

    @NotBlank
    private String name;

    private String gameName;
    private LocalDateTime slotTime;
    private Integer duration;

    @NotNull
    private BigDecimal price;

    public CartItemRequest() {}

    public CartItem.ItemType getType() { return type; }
    public void setType(CartItem.ItemType type) { this.type = type; }

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
}
