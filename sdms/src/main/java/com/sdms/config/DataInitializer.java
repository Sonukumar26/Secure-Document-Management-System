package com.sdms.config;

import com.sdms.model.Role;
import com.sdms.model.User;
import com.sdms.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(
            UserRepository repo,
            PasswordEncoder encoder) {

        return args -> {

            if (repo.findByUsername("admin").isEmpty()) {

                User admin = new User();

                admin.setUsername("admin");

                // 🔐 BCrypt password
                admin.setPassword(
                    encoder.encode("admin123")
                );

                // ✅ ENUM BASED ROLE
                admin.setRole(Role.ADMIN);

                repo.save(admin);

                System.out.println("✅ DEFAULT ADMIN CREATED");
            }

        };
    }
}
