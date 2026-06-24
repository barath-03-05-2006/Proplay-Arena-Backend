package com.proplay.arena.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_registrations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "user_id"}))
public class TournamentRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "registered_at", updatable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    protected void onCreate() { registeredAt = LocalDateTime.now(); }

    public TournamentRegistration() {}

    public Long getId() { return id; }
    public Tournament getTournament() { return tournament; }
    public void setTournament(Tournament tournament) { this.tournament = tournament; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final TournamentRegistration r = new TournamentRegistration();
        public Builder tournament(Tournament t) { r.tournament = t; return this; }
        public Builder user(User u) { r.user = u; return this; }
        public Builder teamName(String name) { r.teamName = name; return this; }
        public TournamentRegistration build() { return r; }
    }
}
