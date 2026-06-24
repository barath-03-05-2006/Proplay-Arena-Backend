package com.proplay.arena.repository;

import com.proplay.arena.entity.TournamentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, Long> {
    List<TournamentRegistration> findByTournamentId(Long tournamentId);
    List<TournamentRegistration> findByUserId(Long userId);
    Optional<TournamentRegistration> findByTournamentIdAndUserId(Long tournamentId, Long userId);
    boolean existsByTournamentIdAndUserId(Long tournamentId, Long userId);
    int countByTournamentId(Long tournamentId);
}
