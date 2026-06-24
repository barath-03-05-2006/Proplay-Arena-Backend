package com.proplay.arena.repository;

import com.proplay.arena.entity.ProPlayer;
import com.proplay.arena.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ProPlayerRepository extends JpaRepository<ProPlayer, Long> {
    Optional<ProPlayer> findByUser(User user);
    Optional<ProPlayer> findByUserId(Long userId);
    List<ProPlayer> findByApprovalStatus(ProPlayer.ApprovalStatus status);
    List<ProPlayer> findByGame(String game);
    List<ProPlayer> findByAvailableTrue();

    @Query("SELECT p FROM ProPlayer p WHERE p.approvalStatus = 'APPROVED' ORDER BY p.rating DESC")
    List<ProPlayer> findTopPlayers();

    @Query("SELECT p FROM ProPlayer p WHERE p.approvalStatus = 'APPROVED' " +
           "AND (:game IS NULL OR p.game = :game) " +
           "AND (:available IS NULL OR p.available = :available)")
    List<ProPlayer> findApprovedByFilters(String game, Boolean available);
}
