package com.proplay.arena.service;

import com.proplay.arena.dto.response.TournamentResponse;
import com.proplay.arena.entity.Tournament;
import com.proplay.arena.entity.TournamentRegistration;
import com.proplay.arena.entity.User;
import com.proplay.arena.exception.BadRequestException;
import com.proplay.arena.exception.ResourceNotFoundException;
import com.proplay.arena.repository.TournamentRegistrationRepository;
import com.proplay.arena.repository.TournamentRepository;
import com.proplay.arena.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public TournamentService(TournamentRepository tournamentRepository,
                             TournamentRegistrationRepository registrationRepository,
                             UserRepository userRepository) {
        this.tournamentRepository = tournamentRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    public List<TournamentResponse> getAll(String status) {
        List<Tournament> tournaments;
        if (status != null) {
            tournaments = tournamentRepository.findByStatus(
                    Tournament.TournamentStatus.valueOf(status.toUpperCase()));
        } else {
            tournaments = tournamentRepository.findAllByOrderByStartDateAsc();
        }
        return tournaments.stream().map(TournamentResponse::from).collect(Collectors.toList());
    }

    public TournamentResponse getById(Long id) {
        return TournamentResponse.from(tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id)));
    }

    public List<TournamentResponse> getMyTournaments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return registrationRepository.findByUserId(user.getId()).stream()
                .map(r -> TournamentResponse.from(r.getTournament()))
                .collect(Collectors.toList());
    }

    @Transactional
    public TournamentResponse join(Long tournamentId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", tournamentId));

        if (tournament.getStatus() != Tournament.TournamentStatus.UPCOMING)
            throw new BadRequestException("Registration is closed for this tournament");
        if (tournament.getRegisteredTeams() >= tournament.getMaxTeams())
            throw new BadRequestException("Tournament is full");
        if (registrationRepository.existsByTournamentIdAndUserId(tournamentId, user.getId()))
            throw new BadRequestException("You are already registered for this tournament");

        TournamentRegistration reg = TournamentRegistration.builder()
                .tournament(tournament).user(user)
                .teamName(user.getUsername() + "'s Team").build();
        registrationRepository.save(reg);
        tournament.setRegisteredTeams(tournament.getRegisteredTeams() + 1);
        return TournamentResponse.from(tournamentRepository.save(tournament));
    }

    @Transactional
    public void leave(Long tournamentId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        TournamentRegistration reg = registrationRepository
                .findByTournamentIdAndUserId(tournamentId, user.getId())
                .orElseThrow(() -> new BadRequestException("You are not registered for this tournament"));
        Tournament tournament = reg.getTournament();
        registrationRepository.delete(reg);
        tournament.setRegisteredTeams(Math.max(0, tournament.getRegisteredTeams() - 1));
        tournamentRepository.save(tournament);
    }

    public List<Object> getBracket(Long tournamentId) {
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", tournamentId));
        return List.of();
    }
}
