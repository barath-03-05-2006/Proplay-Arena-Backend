package com.proplay.arena.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "pro_player_id", nullable = false)
    private ProPlayer proPlayer;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "slot_time", nullable = false)
    private LocalDateTime slotTime;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum BookingStatus { CONFIRMED, COMPLETED, CANCELLED }

    public Booking() {}

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public ProPlayer getProPlayer() { return proPlayer; }
    public void setProPlayer(ProPlayer proPlayer) { this.proPlayer = proPlayer; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public LocalDateTime getSlotTime() { return slotTime; }
    public void setSlotTime(LocalDateTime slotTime) { this.slotTime = slotTime; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Booking b = new Booking();
        public Builder user(User u) { b.user = u; return this; }
        public Builder proPlayer(ProPlayer p) { b.proPlayer = p; return this; }
        public Builder payment(Payment p) { b.payment = p; return this; }
        public Builder slotTime(LocalDateTime t) { b.slotTime = t; return this; }
        public Builder duration(Integer d) { b.duration = d; return this; }
        public Builder amount(BigDecimal a) { b.amount = a; return this; }
        public Builder status(BookingStatus s) { b.status = s; return this; }
        public Booking build() { return b; }
    }
}
