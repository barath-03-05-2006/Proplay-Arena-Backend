package com.proplay.arena.service;

import com.proplay.arena.dto.request.LoginRequest;
import com.proplay.arena.dto.request.RegisterRequest;
import com.proplay.arena.dto.response.AuthResponse;
import com.proplay.arena.entity.User;
import com.proplay.arena.exception.BadRequestException;
import com.proplay.arena.repository.UserRepository;
import com.proplay.arena.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email is already registered");
        if (userRepository.existsByUsername(request.getUsername()))
            throw new BadRequestException("Username is already taken");

        User.Role role = request.getRole() == User.Role.ADMIN ? User.Role.USER : request.getRole();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();
        userRepository.save(user);

        String token = jwtUtil.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
        return buildAuthResponse(token, user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));
        if (user.isBanned())
            throw new BadRequestException("Your account has been banned.");

        String token = jwtUtil.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
        return buildAuthResponse(token, user);
    }

    public AuthResponse.UserResponse getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return toUserResponse(user);
    }

    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder().token(token).user(toUserResponse(user)).build();
    }

    private AuthResponse.UserResponse toUserResponse(User user) {
        return AuthResponse.UserResponse.builder()
                .id(user.getId()).username(user.getUsername())
                .email(user.getEmail()).role(user.getRole()).build();
    }
}
