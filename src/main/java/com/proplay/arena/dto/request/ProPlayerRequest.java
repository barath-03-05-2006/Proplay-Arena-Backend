package com.proplay.arena.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class ProPlayerRequest {

    @NotBlank
    private String game;

    @NotBlank
    private String rank;

    @NotNull @Positive
    private BigDecimal pricePerHour;

    private String bio;
    private boolean available = true;
    private Integer winRate;
    private String responseTime;
    private String availability; // JSON string

    public ProPlayerRequest() {}

    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public BigDecimal getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(BigDecimal pricePerHour) { this.pricePerHour = pricePerHour; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Integer getWinRate() { return winRate; }
    public void setWinRate(Integer winRate) { this.winRate = winRate; }

    public String getResponseTime() { return responseTime; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }
    public void setResponseTime(String responseTime) { this.responseTime = responseTime; }
}
