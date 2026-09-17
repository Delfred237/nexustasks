package com.nexustasks.dev;

import com.nexustasks.user.entity.Role;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String testEmail = "test@nexustasks.com";

        if (userRepository.findByEmail(testEmail).isEmpty()) {
            String rawPassword = "Password123!";

            User testUser = User.builder()
                    .firstName("Test")
                    .lastName("User")
                    .email(testEmail)
                    .passwordHash(passwordEncoder.encode(rawPassword))
                    .role(Role.USER)
                    .enabled(true)
                    .emailVerified(true) // Crucial pour passer la vérification du AuthService
                    .build();

            userRepository.save(testUser);

            log.warn("===============================================");
            log.warn("DEV SEEDER: Created test user");
            log.warn("Email: {}", testEmail);
            log.warn("Password: {}", rawPassword);
            log.warn("===============================================");
        } else {
            log.info("DEV SEEDER: Test user already exists, skipping creation.");
        }
    }
}
