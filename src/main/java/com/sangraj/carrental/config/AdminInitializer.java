package com.sangraj.carrental.config;

import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.Role;
import com.sangraj.carrental.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {

            String adminEmail = "patidartanish31@gmail.com";

            if (repo.existsByEmail(adminEmail)) {
                System.out.println("✔ Admin already exists");
                return;
            }

            AppUser admin = new AppUser(
                    adminEmail,
                    "Tanish",
                    encoder.encode("9165849391"),   // default password
                    Role.ROLE_ADMIN
            );

            repo.save(admin);

            System.out.println("✔ Admin created");
        };
    }
}