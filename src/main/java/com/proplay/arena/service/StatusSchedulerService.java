package com.proplay.arena.service;

import com.proplay.arena.entity.Booking;
import com.proplay.arena.entity.Tournament;
import com.proplay.arena.repository.BookingRepository;
import com.proplay.arena.repository.TournamentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatusSchedulerService {

    private final TournamentRepository tournamentRepository;
    private final BookingRepository bookingRepository;

    public StatusSchedulerService(TournamentRepository tournamentRepository,
                                   BookingRepository bookingRepository) {
        this.tournamentRepository = tournamentRepository;
        this.bookingRepository = bookingRepository;
    }

    // Runs every 10 minutes
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void updateExpiredStatuses() {
        updateTournaments();
        updateBookings();
    }

    private void updateTournaments() {
        LocalDateTime now = LocalDateTime.now();
        List<Tournament> all = tournamentRepository.findAll();

        for (Tournament t : all) {
            if (t.getStatus() == Tournament.TournamentStatus.CANCELLED) continue;

            // If start date passed by more than 2 hours → mark COMPLETED
            if (t.getStartDate() != null && t.getStartDate().plusHours(2).isBefore(now)) {
                if (t.getStatus() == Tournament.TournamentStatus.UPCOMING ||
                    t.getStatus() == Tournament.TournamentStatus.ONGOING) {
                    t.setStatus(Tournament.TournamentStatus.COMPLETED);
                    tournamentRepository.save(t);
                }
            }
            // If start date just passed → mark ONGOING
            else if (t.getStartDate() != null && t.getStartDate().isBefore(now)) {
                if (t.getStatus() == Tournament.TournamentStatus.UPCOMING) {
                    t.setStatus(Tournament.TournamentStatus.ONGOING);
                    tournamentRepository.save(t);
                }
            }
        }
    }

    private void updateBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> confirmed = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .toList();

        for (Booking b : confirmed) {
            if (b.getSlotTime() == null) continue;
            int duration = b.getDuration() != null ? b.getDuration() : 1;
            LocalDateTime endTime = b.getSlotTime().plusHours(duration);

            // If booking end time has passed → mark COMPLETED
            if (endTime.isBefore(now)) {
                b.setStatus(Booking.BookingStatus.COMPLETED);
                bookingRepository.save(b);
            }
        }
    }
}
