package com.example.crmTelco.config;

import com.example.crmTelco.entity.User;
import com.example.crmTelco.entity.Package;
import com.example.crmTelco.repository.UserRepository;
import com.example.crmTelco.repository.PackageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user if not exists
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@telco.com");
                admin.setFullName("System Administrator");
                admin.setRole(User.Role.ADMIN);
                admin.setEnabled(true);
                admin.setAddress("123 Tech Park, Colombo 01, Sri Lanka");
                admin.setMsisdn("+94770000000");
                admin.setCategory(User.Category.POSTPAID);
                admin.setContactDetails("Office: +94112345678, Email: support@telco.com");
                userRepository.save(admin);
                System.out.println("Admin user created: admin/admin123");
            }

            // Create sample users if none exist
            if (userRepository.count() == 1) {
                // Prepaid user
                User prepaidUser = new User();
                prepaidUser.setUsername("john_doe");
                prepaidUser.setPassword(passwordEncoder.encode("user123"));
                prepaidUser.setEmail("john.doe@example.com");
                prepaidUser.setFullName("John Doe");
                prepaidUser.setRole(User.Role.USER);
                prepaidUser.setEnabled(true);
                prepaidUser.setAddress("456 Main Street, Kandy, Sri Lanka");
                prepaidUser.setMsisdn("+94771234567");
                prepaidUser.setCategory(User.Category.PREPAID);
                prepaidUser.setContactDetails("Personal: +94711234567, Work: john@company.com");
                userRepository.save(prepaidUser);

                // Postpaid user
                User postpaidUser = new User();
                postpaidUser.setUsername("jane_smith");
                postpaidUser.setPassword(passwordEncoder.encode("user123"));
                postpaidUser.setEmail("jane.smith@example.com");
                postpaidUser.setFullName("Jane Smith");
                postpaidUser.setRole(User.Role.USER);
                postpaidUser.setEnabled(true);
                postpaidUser.setAddress("789 Beach Road, Galle, Sri Lanka");
                postpaidUser.setMsisdn("+94772345678");
                postpaidUser.setCategory(User.Category.POSTPAID);
                postpaidUser.setContactDetails("Office: +94122345678, Home: +94712345678");
                userRepository.save(postpaidUser);

                System.out.println("Sample users created");
            }
        };
    }
}
