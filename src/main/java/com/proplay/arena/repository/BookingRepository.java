package com.proplay.arena.repository;

import com.proplay.arena.entity.Booking;
import com.proplay.arena.entity.ProPlayer;
import com.proplay.arena.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserOrderByCreatedAtDesc(User user);
    List<Booking> findByProPlayerOrderByCreatedAtDesc(ProPlayer proPlayer);
    List<Booking> findByProPlayerAndStatus(ProPlayer proPlayer, Booking.BookingStatus status);
    int countByProPlayer(ProPlayer proPlayer);

    // Get all active bookings for a player between start and end of a day
    @Query("SELECT b FROM Booking b WHERE b.proPlayer.id = :playerId " +
           "AND b.status != 'CANCELLED' " +
           "AND b.slotTime >= :dayStart " +
           "AND b.slotTime < :dayEnd")
    List<Booking> findByProPlayerIdAndDate(
            @Param("playerId") Long playerId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);

    // Check slot conflict using time range overlap
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.proPlayer.id = :playerId " +
           "AND b.status != 'CANCELLED' " +
           "AND b.slotTime < :slotEnd " +
           "AND b.slotTime >= :slotStart")
    boolean isSlotConflicting(
            @Param("playerId") Long playerId,
            @Param("slotStart") LocalDateTime slotStart,
            @Param("slotEnd") LocalDateTime slotEnd);
}
