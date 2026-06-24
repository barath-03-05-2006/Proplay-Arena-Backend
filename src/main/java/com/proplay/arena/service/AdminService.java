package com.proplay.arena.service;

import com.proplay.arena.dto.request.TournamentRequest;
import com.proplay.arena.dto.response.*;
import com.proplay.arena.entity.*;
import com.proplay.arena.exception.BadRequestException;
import com.proplay.arena.exception.ResourceNotFoundException;
import com.proplay.arena.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ProPlayerRepository proPlayerRepository;
    private final TournamentRepository tournamentRepository;
    private final PaymentRepository paymentRepository;
    private final TournamentRegistrationRepository registrationRepository;

    public AdminService(UserRepository userRepository, ProPlayerRepository proPlayerRepository,
                        TournamentRepository tournamentRepository, PaymentRepository paymentRepository,
                        TournamentRegistrationRepository registrationRepository) {
        this.userRepository = userRepository;
        this.proPlayerRepository = proPlayerRepository;
        this.tournamentRepository = tournamentRepository;
        this.paymentRepository = paymentRepository;
        this.registrationRepository = registrationRepository;
    }

    public AdminStatsResponse getStats() {
    	LocalDate today = LocalDate.now();

    	List<Tournament> all = tournamentRepository.findAll();

    	for (Tournament t : all) {
    	    if (t.getStartDate() == null) continue;

    	    LocalDate start = t.getStartDate().toLocalDate();

    	    if (start.equals(today)) {
    	        t.setStatus(Tournament.TournamentStatus.ONGOING);
    	    } else if (start.isBefore(today)) {
    	        t.setStatus(Tournament.TournamentStatus.COMPLETED);
    	    } else {
    	        t.setStatus(Tournament.TournamentStatus.UPCOMING);
    	    }
    	}

    	tournamentRepository.saveAll(all);
        return AdminStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalProPlayers(proPlayerRepository.count())
                .pendingApprovals(proPlayerRepository.findByApprovalStatus(ProPlayer.ApprovalStatus.PENDING).size())
                .totalTournaments(tournamentRepository.count())
                .activeTournaments(tournamentRepository.findByStatus(Tournament.TournamentStatus.ONGOING).size())
                .totalPayments(paymentRepository.count())
                .totalRevenue(paymentRepository.getTotalRevenue())
                .build();
    }

    public List<PlayerResponse> getAllPlayers(String status) {
        List<ProPlayer> players = status != null
                ? proPlayerRepository.findByApprovalStatus(ProPlayer.ApprovalStatus.valueOf(status.toUpperCase()))
                : proPlayerRepository.findAll();
        return players.stream().map(PlayerResponse::from).collect(Collectors.toList());
    }

    public List<PlayerResponse> getPendingPlayers() {
        return proPlayerRepository.findByApprovalStatus(ProPlayer.ApprovalStatus.PENDING)
                .stream().map(PlayerResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public PlayerResponse approvePlayer(Long id) {
        ProPlayer player = proPlayerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", id));
        player.setApprovalStatus(ProPlayer.ApprovalStatus.APPROVED);
        player.setVerified(true);
        return PlayerResponse.from(proPlayerRepository.save(player));
    }

    @Transactional
    public PlayerResponse rejectPlayer(Long id) {
        ProPlayer player = proPlayerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", id));
        player.setApprovalStatus(ProPlayer.ApprovalStatus.REJECTED);
        player.setVerified(false);
        return PlayerResponse.from(proPlayerRepository.save(player));
    }

    public List<TournamentResponse> getAllTournaments(String status) {
        List<Tournament> tournaments = status != null
                ? tournamentRepository.findByStatus(Tournament.TournamentStatus.valueOf(status.toUpperCase()))
                : tournamentRepository.findAll();
        return tournaments.stream().map(TournamentResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public TournamentResponse createTournament(TournamentRequest req, Long adminId) {
        // Block past dates
        if (req.getStartDate() != null && req.getStartDate().isBefore(LocalDateTime.now().toLocalDate().atStartOfDay())) {
            throw new BadRequestException("Cannot create a tournament with a past date. Please select today or a future date.");
        }
        Tournament t = Tournament.builder()
                .name(req.getName()).game(req.getGame()).entryFee(req.getEntryFee())
                .prizePool(req.getPrizePool()).maxTeams(req.getMaxTeams())
                .startDate(req.getStartDate()).description(req.getDescription())
                .rules(req.getRules()).bannerUrl(req.getBannerUrl())
                .status(Tournament.TournamentStatus.UPCOMING).createdBy(adminId).build();
        return TournamentResponse.from(tournamentRepository.save(t));
    }

    @Transactional
    public TournamentResponse updateTournament(Long id, TournamentRequest req) {
        Tournament t = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));
        // Block setting a past date (only validate if changing the date)
        if (req.getStartDate() != null && req.getStartDate().isBefore(LocalDateTime.now().toLocalDate().atStartOfDay())) {
            throw new BadRequestException("Cannot set a past date for a tournament.");
        }
        t.setName(req.getName()); t.setGame(req.getGame());
        t.setEntryFee(req.getEntryFee()); t.setPrizePool(req.getPrizePool());
        t.setMaxTeams(req.getMaxTeams()); t.setStartDate(req.getStartDate());
        t.setDescription(req.getDescription()); t.setRules(req.getRules());
        if (req.getBannerUrl() != null) t.setBannerUrl(req.getBannerUrl());
        return TournamentResponse.from(tournamentRepository.save(t));
    }

    @Transactional
    public void deleteTournament(Long id) {
        tournamentRepository.delete(tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id)));
    }

    @Transactional
    public TournamentResponse declareWinner(Long tournamentId, String winnerTeam) {
        Tournament t = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", tournamentId));
        t.setWinnerTeam(winnerTeam);
        t.setStatus(Tournament.TournamentStatus.COMPLETED);
        return TournamentResponse.from(tournamentRepository.save(t));
    }

    public List<Map<String, Object>> getAllUsers(String role) {
        List<User> users = role != null
                ? userRepository.findAll().stream()
                    .filter(u -> u.getRole() == User.Role.valueOf(role.toUpperCase()))
                    .collect(Collectors.toList())
                : userRepository.findAll();
        return users.stream().map(u -> Map.of(
                "id", (Object) u.getId(), "username", u.getUsername(),
                "email", u.getEmail(), "role", u.getRole().name(),
                "banned", u.isBanned(), "createdAt", u.getCreatedAt().toString()
        )).collect(Collectors.toList());
    }

    @Transactional
    public ApiResponse banUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
        if (user.getRole() == User.Role.ADMIN) throw new BadRequestException("Cannot ban an admin");
        user.setBanned(true);
        userRepository.save(user);
        return ApiResponse.ok("User banned successfully");
    }

    @Transactional
    public ApiResponse unbanUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setBanned(false);
        userRepository.save(user);
        return ApiResponse.ok("User unbanned successfully");
    }

    public List<Map<String, Object>> getAllPayments(String status) {
        return paymentRepository.findAll().stream()
                .filter(p -> status == null || p.getStatus().name().equalsIgnoreCase(status))
                .map(p -> Map.of(
                        "id", (Object) p.getId(), "userId", p.getUser().getId(),
                        "username", p.getUser().getUsername(),
                        "razorpayOrderId", p.getRazorpayOrderId() != null ? p.getRazorpayOrderId() : "",
                        "amount", p.getAmount(), "status", p.getStatus().name(),
                        "createdAt", p.getCreatedAt().toString()))
                .collect(Collectors.toList());
    }
}
