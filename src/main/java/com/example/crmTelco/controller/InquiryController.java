package com.example.crmTelco.controller;

import com.example.crmTelco.entity.Inquiry;
import com.example.crmTelco.entity.User;
import com.example.crmTelco.service.InquiryService;
import com.example.crmTelco.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;
    private final UserService userService;

    public InquiryController(InquiryService inquiryService, UserService userService) {
        this.inquiryService = inquiryService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createInquiry(@RequestBody Map<String, Object> inquiryData, Authentication authentication) {
        try {
            // Get current user
            User currentUser = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create inquiry
            Inquiry inquiry = new Inquiry();
            inquiry.setUser(currentUser);
            inquiry.setSubject((String) inquiryData.get("subject"));
            inquiry.setMessage((String) inquiryData.get("message"));
            inquiry.setType(Inquiry.InquiryType.valueOf(((String) inquiryData.get("type")).toUpperCase()));
            
            Inquiry createdInquiry = inquiryService.createInquiry(inquiry);
            return ResponseEntity.ok(createdInquiry);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/my-inquiries")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Inquiry>> getMyInquiries(Authentication authentication) {
        try {
            User currentUser = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Inquiry> inquiries = inquiryService.getInquiriesByUserId(currentUser.getId());
            return ResponseEntity.ok(inquiries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/my-inquiries/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMyInquiryById(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            return inquiryService.getInquiryById(id)
                    .map(inquiry -> {
                        // Verify that the inquiry belongs to the current user
                        if (inquiry.getUser().getId().equals(currentUser.getId())) {
                            return ResponseEntity.<Inquiry>ok(inquiry);
                        } else {
                            return ResponseEntity.<Inquiry>badRequest().build();
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/my-inquiries/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateMyInquiry(@PathVariable Long id, 
                                            @RequestBody Map<String, Object> inquiryData, 
                                            Authentication authentication) {
        try {
            User currentUser = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            return inquiryService.getInquiryById(id)
                    .map(inquiry -> {
                        // Verify that the inquiry belongs to the current user
                        if (!inquiry.getUser().getId().equals(currentUser.getId())) {
                            return ResponseEntity.<Map<String, String>>badRequest()
                                    .body(Map.of("error", "You can only update your own inquiries"));
                        }
                        
                        // Only allow updating subject and message, not status or admin response
                        if (inquiryData.containsKey("subject")) {
                            inquiry.setSubject((String) inquiryData.get("subject"));
                        }
                        if (inquiryData.containsKey("message")) {
                            inquiry.setMessage((String) inquiryData.get("message"));
                        }
                        
                        Inquiry updatedInquiry = inquiryService.updateInquiry(id, inquiry);
                        return ResponseEntity.ok(updatedInquiry);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/types")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getInquiryTypes() {
        Map<String, Object> response = new HashMap<>();
        response.put("types", Inquiry.InquiryType.values());
        return ResponseEntity.ok(response);
    }
}
