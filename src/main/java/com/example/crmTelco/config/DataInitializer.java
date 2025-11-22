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
                                    PackageRepository packageRepository,
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
                userRepository.save(admin);
                System.out.println("Admin user created: admin/admin123");
            }

            // Create sample packages if none exist
            if (packageRepository.count() == 0) {
                Package basicPackage = new Package();
                basicPackage.setName("Basic");
                basicPackage.setDescription("Basic mobile package with limited features");
                basicPackage.setPrice(new BigDecimal("29.99"));
                basicPackage.setDataLimitGB(5);
                basicPackage.setVoiceMinutes(500);
                basicPackage.setSmsCount(100);
                basicPackage.setActive(true);
                packageRepository.save(basicPackage);

                Package premiumPackage = new Package();
                premiumPackage.setName("Premium");
                premiumPackage.setDescription("Premium mobile package with extensive features");
                premiumPackage.setPrice(new BigDecimal("59.99"));
                premiumPackage.setDataLimitGB(50);
                premiumPackage.setVoiceMinutes(2000);
                premiumPackage.setSmsCount(500);
                premiumPackage.setActive(true);
                packageRepository.save(premiumPackage);

                Package unlimitedPackage = new Package();
                unlimitedPackage.setName("Unlimited");
                unlimitedPackage.setDescription("Unlimited mobile package for heavy users");
                unlimitedPackage.setPrice(new BigDecimal("99.99"));
                unlimitedPackage.setDataLimitGB(999);
                unlimitedPackage.setVoiceMinutes(9999);
                unlimitedPackage.setSmsCount(9999);
                unlimitedPackage.setActive(true);
                packageRepository.save(unlimitedPackage);

                System.out.println("Sample packages created");
            }
        };
    }
}
