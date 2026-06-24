package com.proplay.arena.config;

import com.proplay.arena.entity.User;
import com.proplay.arena.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@proplay.com";
        if (userRepository.existsByEmail(adminEmail)) return;

        User admin = User.builder()
                .username("admin")
                .email(adminEmail)
                .password(passwordEncoder.encode("Admin@1234"))
                .role(User.Role.ADMIN)
                .active(true)
                .banned(false)
                .build();

        userRepository.save(admin);
        System.out.println("========================================");
        System.out.println("  Admin account created:");
        System.out.println("  Email   : admin@proplay.com");
        System.out.println("  Password: Admin@1234");
        System.out.println("========================================");
    }
}
