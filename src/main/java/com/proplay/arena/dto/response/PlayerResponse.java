package com.proplay.arena.dto.response;

import com.proplay.arena.entity.ProPlayer;
import java.math.BigDecimal;

public class PlayerResponse {

    private Long id;
    private Long userId;
    private String username;
    private String game;
    private String rank;
    private BigDecimal pricePerHour;
    private String bio;
    private boolean verified;
    private boolean available;
    private BigDecimal rating;
    private int totalRatings;
    private int totalSessions;
    private Integer winRate;
    private String responseTime;
    private ProPlayer.ApprovalStatus approvalStatus;
    private String availability;

    public PlayerResponse() {}

    public static PlayerResponse from(ProPlayer p) {
        PlayerResponse r = new PlayerResponse();
        r.id = p.getId();
        r.userId = p.getUser().getId();
        r.username = p.getUser().getUsername();
        r.game = p.getGame();
        r.rank = p.getRank();
        r.pricePerHour = p.getPricePerHour();
        r.bio = p.getBio();
        r.verified = p.isVerified();
        r.available = p.isAvailable();
        r.rating = p.getRating();
        r.totalRatings = p.getTotalRatings();
        r.totalSessions = p.getTotalSessions();
        r.winRate = p.getWinRate();
        r.responseTime = p.getResponseTime();
        r.approvalStatus = p.getApprovalStatus();
        r.availability = p.getAvailability();
        return r;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getGame() { return game; }
    public String getRank() { return rank; }
    public BigDecimal getPricePerHour() { return pricePerHour; }
    public String getBio() { return bio; }
    public boolean isVerified() { return verified; }
    public boolean isAvailable() { return available; }
    public BigDecimal getRating() { return rating; }
    public int getTotalRatings() { return totalRatings; }
    public int getTotalSessions() { return totalSessions; }
    public Integer getWinRate() { return winRate; }
    public String getResponseTime() { return responseTime; }
    public ProPlayer.ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public String getAvailability() { return availability; }
}
