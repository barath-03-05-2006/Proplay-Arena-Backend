package com.proplay.arena.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pro_players")
public class ProPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String game;

    // "rank" is a reserved word in MySQL — use column name "player_rank"
    @Column(name = "player_rank", nullable = false)
    private String rank;

    @Column(name = "price_per_hour", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerHour;

    @Column(length = 500)
    private String bio;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "total_ratings")
    private int totalRatings = 0;

    @Column(name = "total_sessions")
    private int totalSessions = 0;

    @Column(name = "win_rate")
    private Integer winRate;

    @Column(name = "response_time", length = 20)
    private String responseTime = "Fast";

    // JSON string storing weekly availability e.g. {"Monday":["09:00","11:00"],"Tuesday":["10:00"]}
    @Column(name = "availability", columnDefinition = "TEXT")
    private String availability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum ApprovalStatus { PENDING, APPROVED, REJECTED }

    // ---- Constructors ----
    public ProPlayer() {}

    // ---- Getters & Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public BigDecimal getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(BigDecimal pricePerHour) { this.pricePerHour = pricePerHour; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }

    public Integer getWinRate() { return winRate; }
    public void setWinRate(Integer winRate) { this.winRate = winRate; }

    public String getResponseTime() { return responseTime; }
    public void setResponseTime(String responseTime) { this.responseTime = responseTime; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    // ---- Builder ----
    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ProPlayer p = new ProPlayer();
        public Builder user(User user) { p.user = user; return this; }
        public Builder game(String game) { p.game = game; return this; }
        public Builder rank(String rank) { p.rank = rank; return this; }
        public Builder pricePerHour(BigDecimal pricePerHour) { p.pricePerHour = pricePerHour; return this; }
        public Builder bio(String bio) { p.bio = bio; return this; }
        public Builder verified(boolean verified) { p.verified = verified; return this; }
        public Builder available(boolean available) { p.available = available; return this; }
        public Builder rating(BigDecimal rating) { p.rating = rating; return this; }
        public Builder winRate(Integer winRate) { p.winRate = winRate; return this; }
        public Builder responseTime(String responseTime) { p.responseTime = responseTime; return this; }
        public Builder availability(String availability) { p.availability = availability; return this; }
        public Builder approvalStatus(ApprovalStatus approvalStatus) { p.approvalStatus = approvalStatus; return this; }
        public ProPlayer build() { return p; }
    }
}
