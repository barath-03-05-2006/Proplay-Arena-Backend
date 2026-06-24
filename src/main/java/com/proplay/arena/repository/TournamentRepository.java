package com.proplay.arena.repository;

import com.proplay.arena.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByStatus(Tournament.TournamentStatus status);
    List<Tournament> findByGame(String game);
    List<Tournament> findAllByOrderByStartDateAsc();
}
