package com.example.crmTelco.config;

import com.example.crmTelco.entity.User;
import com.example.crmTelco.entity.Inquiry;
import com.example.crmTelco.repository.UserRepository;
import com.example.crmTelco.repository.InquiryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                    InquiryRepository inquiryRepository,
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

            // Create sample inquiries if none exist
            if (inquiryRepository.count() == 0) {
                User john = userRepository.findByUsername("john_doe")
                    .orElseThrow(() -> new RuntimeException("User john_doe not found"));
                User jane = userRepository.findByUsername("jane_smith")
                    .orElseThrow(() -> new RuntimeException("User jane_smith not found"));

                // Billing inquiry from John
                Inquiry billingInquiry = new Inquiry();
                billingInquiry.setUser(john);
                billingInquiry.setSubject("Question about my monthly bill");
                billingInquiry.setMessage("I noticed my bill this month is higher than usual. Can you please explain the charges? I was charged for international calls that I don't remember making.");
                billingInquiry.setType(Inquiry.InquiryType.BILLING);
                billingInquiry.setStatus(Inquiry.InquiryStatus.PENDING);
                inquiryRepository.save(billingInquiry);

                // Technical inquiry from Jane
                Inquiry technicalInquiry = new Inquiry();
                technicalInquiry.setUser(jane);
                technicalInquiry.setSubject("Poor network coverage in my area");
                technicalInquiry.setMessage("For the past week, I've been experiencing very poor network coverage at my home in Galle. The signal strength is very weak and I frequently drop calls. Is there any maintenance work going on?");
                technicalInquiry.setType(Inquiry.InquiryType.TECHNICAL);
                technicalInquiry.setStatus(Inquiry.InquiryStatus.IN_PROGRESS);
                technicalInquiry.setAdminResponse("We apologize for the inconvenience. Our technical team is investigating the issue in your area.");
                inquiryRepository.save(technicalInquiry);

                // General inquiry from John
                Inquiry generalInquiry = new Inquiry();
                generalInquiry.setUser(john);
                generalInquiry.setSubject("Information about new packages");
                generalInquiry.setMessage("I heard about your new 5G packages. Can you provide me with more information about the available options and pricing? I'm particularly interested in unlimited data plans.");
                generalInquiry.setType(Inquiry.InquiryType.GENERAL);
                generalInquiry.setStatus(Inquiry.InquiryStatus.RESOLVED);
                generalInquiry.setAdminResponse("Thank you for your interest! We have three 5G packages available: Basic (50GB for LKR 1499), Plus (100GB for LKR 2499), and Unlimited (for LKR 3999). All include free voice minutes and SMS. Would you like me to upgrade your current package?");
                inquiryRepository.save(generalInquiry);

                // Complaint from Jane
                Inquiry complaintInquiry = new Inquiry();
                complaintInquiry.setUser(jane);
                complaintInquiry.setSubject("Customer service complaint");
                complaintInquiry.setMessage("I waited 45 minutes on the phone yesterday to speak with a customer service representative, and then the call was disconnected. This is very frustrating and has happened multiple times.");
                complaintInquiry.setType(Inquiry.InquiryType.COMPLAINT);
                complaintInquiry.setStatus(Inquiry.InquiryStatus.PENDING);
                inquiryRepository.save(complaintInquiry);

                // Service request from John
                Inquiry requestInquiry = new Inquiry();
                requestInquiry.setUser(john);
                requestInquiry.setSubject("Request for SIM replacement");
                requestInquiry.setMessage("My SIM card was damaged and I need a replacement. I've already visited your Kandy branch but they were out of stock. Can you arrange for a SIM replacement at the Colombo main branch?");
                requestInquiry.setType(Inquiry.InquiryType.REQUEST);
                requestInquiry.setStatus(Inquiry.InquiryStatus.IN_PROGRESS);
                requestInquiry.setAdminResponse("We apologize for the inconvenience. A new SIM card has been reserved for you at our Colombo main branch. You can collect it anytime during business hours tomorrow.");
                inquiryRepository.save(requestInquiry);

                // Feedback from Jane
                Inquiry feedbackInquiry = new Inquiry();
                feedbackInquiry.setUser(jane);
                feedbackInquiry.setSubject("Positive feedback on service");
                feedbackInquiry.setMessage("I just wanted to commend your technician who visited my home yesterday to fix my internet connection. He was very professional, knowledgeable, and resolved the issue quickly. Excellent service!");
                feedbackInquiry.setType(Inquiry.InquiryType.FEEDBACK);
                feedbackInquiry.setStatus(Inquiry.InquiryStatus.RESOLVED);
                feedbackInquiry.setAdminResponse("Thank you for your positive feedback! We're delighted to hear about your great experience. We'll share your comments with the technician and his team.");
                inquiryRepository.save(feedbackInquiry);

                System.out.println("Sample inquiries created");
            }
        };
    }
}
