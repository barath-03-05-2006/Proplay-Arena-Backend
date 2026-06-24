package com.proplay.arena.dto.response;

import java.math.BigDecimal;

public class AdminStatsResponse {

    private long totalUsers;
    private long totalProPlayers;
    private long pendingApprovals;
    private long totalTournaments;
    private long activeTournaments;
    private long totalPayments;
    private BigDecimal totalRevenue;

    public AdminStatsResponse() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalProPlayers() { return totalProPlayers; }
    public void setTotalProPlayers(long totalProPlayers) { this.totalProPlayers = totalProPlayers; }

    public long getPendingApprovals() { return pendingApprovals; }
    public void setPendingApprovals(long pendingApprovals) { this.pendingApprovals = pendingApprovals; }

    public long getTotalTournaments() { return totalTournaments; }
    public void setTotalTournaments(long totalTournaments) { this.totalTournaments = totalTournaments; }

    public long getActiveTournaments() { return activeTournaments; }
    public void setActiveTournaments(long activeTournaments) { this.activeTournaments = activeTournaments; }

    public long getTotalPayments() { return totalPayments; }
    public void setTotalPayments(long totalPayments) { this.totalPayments = totalPayments; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final AdminStatsResponse r = new AdminStatsResponse();
        public Builder totalUsers(long v) { r.totalUsers = v; return this; }
        public Builder totalProPlayers(long v) { r.totalProPlayers = v; return this; }
        public Builder pendingApprovals(long v) { r.pendingApprovals = v; return this; }
        public Builder totalTournaments(long v) { r.totalTournaments = v; return this; }
        public Builder activeTournaments(long v) { r.activeTournaments = v; return this; }
        public Builder totalPayments(long v) { r.totalPayments = v; return this; }
        public Builder totalRevenue(BigDecimal v) { r.totalRevenue = v; return this; }
        public AdminStatsResponse build() { return r; }
    }
}
