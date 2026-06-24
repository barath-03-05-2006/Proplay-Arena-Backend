package com.proplay.arena.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String game;

    @Column(name = "entry_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal entryFee;

    @Column(name = "prize_pool", nullable = false, precision = 10, scale = 2)
    private BigDecimal prizePool;

    @Column(name = "max_teams", nullable = false)
    private int maxTeams;

    @Column(name = "registered_teams", nullable = false)
    private int registeredTeams = 0;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(length = 1000)
    private String description;

    @Column(length = 2000)
    private String rules;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status = TournamentStatus.UPCOMING;

    @Column(name = "winner_team")
    private String winnerTeam;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum TournamentStatus { UPCOMING, ONGOING, COMPLETED, CANCELLED }

    public Tournament() {}

    // Getters & Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }
    public BigDecimal getEntryFee() { return entryFee; }
    public void setEntryFee(BigDecimal entryFee) { this.entryFee = entryFee; }
    public BigDecimal getPrizePool() { return prizePool; }
    public void setPrizePool(BigDecimal prizePool) { this.prizePool = prizePool; }
    public int getMaxTeams() { return maxTeams; }
    public void setMaxTeams(int maxTeams) { this.maxTeams = maxTeams; }
    public int getRegisteredTeams() { return registeredTeams; }
    public void setRegisteredTeams(int registeredTeams) { this.registeredTeams = registeredTeams; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }
    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    public TournamentStatus getStatus() { return status; }
    public void setStatus(TournamentStatus status) { this.status = status; }
    public String getWinnerTeam() { return winnerTeam; }
    public void setWinnerTeam(String winnerTeam) { this.winnerTeam = winnerTeam; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Tournament t = new Tournament();
        public Builder name(String v) { t.name = v; return this; }
        public Builder game(String v) { t.game = v; return this; }
        public Builder entryFee(BigDecimal v) { t.entryFee = v; return this; }
        public Builder prizePool(BigDecimal v) { t.prizePool = v; return this; }
        public Builder maxTeams(int v) { t.maxTeams = v; return this; }
        public Builder startDate(LocalDateTime v) { t.startDate = v; return this; }
        public Builder description(String v) { t.description = v; return this; }
        public Builder rules(String v) { t.rules = v; return this; }
        public Builder bannerUrl(String v) { t.bannerUrl = v; return this; }
        public Builder status(TournamentStatus v) { t.status = v; return this; }
        public Builder createdBy(Long v) { t.createdBy = v; return this; }
        public Tournament build() { return t; }
    }
}
