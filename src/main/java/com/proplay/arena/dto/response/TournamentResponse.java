package com.proplay.arena.dto.response;

import com.proplay.arena.entity.Tournament;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TournamentResponse {

    private Long id;
    private String name;
    private String game;
    private BigDecimal entryFee;
    private BigDecimal prizePool;
    private int maxTeams;
    private int registeredTeams;
    private LocalDateTime startDate;
    private String description;
    private String rules;
    private String bannerUrl;
    private Tournament.TournamentStatus status;
    private String winnerTeam;

    public TournamentResponse() {}

    public static TournamentResponse from(Tournament t) {
        TournamentResponse r = new TournamentResponse();
        r.id = t.getId();
        r.name = t.getName();
        r.game = t.getGame();
        r.entryFee = t.getEntryFee();
        r.prizePool = t.getPrizePool();
        r.maxTeams = t.getMaxTeams();
        r.registeredTeams = t.getRegisteredTeams();
        r.startDate = t.getStartDate();
        r.description = t.getDescription();
        r.rules = t.getRules();
        r.bannerUrl = t.getBannerUrl();
        r.status = t.getStatus();
        r.winnerTeam = t.getWinnerTeam();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getGame() { return game; }
    public BigDecimal getEntryFee() { return entryFee; }
    public BigDecimal getPrizePool() { return prizePool; }
    public int getMaxTeams() { return maxTeams; }
    public int getRegisteredTeams() { return registeredTeams; }
    public LocalDateTime getStartDate() { return startDate; }
    public String getDescription() { return description; }
    public String getRules() { return rules; }
    public String getBannerUrl() { return bannerUrl; }
    public Tournament.TournamentStatus getStatus() { return status; }
    public String getWinnerTeam() { return winnerTeam; }
}
