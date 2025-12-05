package com.example.crmTelco.controller;

import com.example.crmTelco.entity.User;
import com.example.crmTelco.entity.Package;
import com.example.crmTelco.entity.Inquiry;
import com.example.crmTelco.service.UserService;
import com.example.crmTelco.service.PackageService;
import com.example.crmTelco.service.InquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final UserService userService;
    private final PackageService packageService;
    private final InquiryService inquiryService;

    public AdminApiController(UserService userService, PackageService packageService, InquiryService inquiryService) {
        this.userService = userService;
        this.packageService = packageService;
        this.inquiryService = inquiryService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getTotalUsersCount());
        stats.put("totalPackages", packageService.getTotalPackagesCount());
        stats.put("activePackages", packageService.getActivePackagesCount());
        stats.put("totalInquiries", inquiryService.getTotalInquiriesCount());
        stats.put("pendingInquiries", inquiryService.getInquiriesCountByStatus(Inquiry.InquiryStatus.PENDING));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        try {
            User user = userService.toggleUserStatus(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/packages")
    public ResponseEntity<List<Package>> getAllPackages() {
        return ResponseEntity.ok(packageService.getAllPackages());
    }

    @GetMapping("/packages/{id}")
    public ResponseEntity<Package> getPackageById(@PathVariable Long id) {
        return packageService.getPackageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/packages")
    public ResponseEntity<?> createPackage(@RequestBody Package packageEntity) {
        try {
            Package createdPackage = packageService.createPackage(packageEntity);
            return ResponseEntity.ok(createdPackage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/packages/{id}")
    public ResponseEntity<?> updatePackage(@PathVariable Long id, @RequestBody Package packageDetails) {
        try {
            Package updatedPackage = packageService.updatePackage(id, packageDetails);
            return ResponseEntity.ok(updatedPackage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/packages/{id}/toggle-status")
    public ResponseEntity<?> togglePackageStatus(@PathVariable Long id) {
        try {
            Package packageEntity = packageService.togglePackageStatus(id);
            return ResponseEntity.ok("Package status updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/packages/{id}")
    public ResponseEntity<?> deletePackage(@PathVariable Long id) {
        try {
            packageService.deletePackage(id);
            return ResponseEntity.ok("Package deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        return userService.getUserByUsername(authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/category/{category}")
    public ResponseEntity<List<User>> getUsersByCategory(@PathVariable String category) {
        try {
            User.Category cat = User.Category.valueOf(category.toUpperCase());
            return ResponseEntity.ok(userService.getUsersByCategory(cat));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/role/{role}/category/{category}")
    public ResponseEntity<List<User>> getUsersByRoleAndCategory(@PathVariable String role, @PathVariable String category) {
        try {
            User.Role r = User.Role.valueOf(role.toUpperCase());
            User.Category cat = User.Category.valueOf(category.toUpperCase());
            return ResponseEntity.ok(userService.getUsersByRoleAndCategory(r, cat));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/msisdn/{msisdn}")
    public ResponseEntity<User> getUserByMsisdn(@PathVariable String msisdn) {
        return userService.getUserByMsisdn(msisdn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Inquiry endpoints
    @GetMapping("/inquiries")
    public ResponseEntity<List<Inquiry>> getAllInquiries() {
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }

    @GetMapping("/inquiries/{id}")
    public ResponseEntity<Inquiry> getInquiryById(@PathVariable Long id) {
        return inquiryService.getInquiryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/inquiries")
    public ResponseEntity<?> createInquiry(@RequestBody Inquiry inquiry) {
        try {
            Inquiry createdInquiry = inquiryService.createInquiry(inquiry);
            return ResponseEntity.ok(createdInquiry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/inquiries/{id}")
    public ResponseEntity<?> updateInquiry(@PathVariable Long id, @RequestBody Inquiry inquiryDetails) {
        try {
            Inquiry updatedInquiry = inquiryService.updateInquiry(id, inquiryDetails);
            return ResponseEntity.ok(updatedInquiry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/inquiries/{id}/status")
    public ResponseEntity<?> updateInquiryStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        try {
            Inquiry updatedInquiry = inquiryService.updateInquiryStatus(id, statusMap.get("status"));
            return ResponseEntity.ok(updatedInquiry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/inquiries/{id}/respond")
    public ResponseEntity<?> respondToInquiry(@PathVariable Long id, @RequestBody Map<String, String> responseMap) {
        try {
            Inquiry updatedInquiry = inquiryService.respondToInquiry(id, responseMap.get("response"));
            return ResponseEntity.ok(updatedInquiry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/inquiries/{id}")
    public ResponseEntity<?> deleteInquiry(@PathVariable Long id) {
        try {
            inquiryService.deleteInquiry(id);
            return ResponseEntity.ok("Inquiry deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/inquiries/user/{userId}")
    public ResponseEntity<List<Inquiry>> getInquiriesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(inquiryService.getInquiriesByUserId(userId));
    }

    @GetMapping("/inquiries/status/{status}")
    public ResponseEntity<List<Inquiry>> getInquiriesByStatus(@PathVariable String status) {
        try {
            Inquiry.InquiryStatus inquiryStatus = Inquiry.InquiryStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(inquiryService.getInquiriesByStatus(inquiryStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/inquiries/type/{type}")
    public ResponseEntity<List<Inquiry>> getInquiriesByType(@PathVariable String type) {
        try {
            Inquiry.InquiryType inquiryType = Inquiry.InquiryType.valueOf(type.toUpperCase());
            return ResponseEntity.ok(inquiryService.getInquiriesByType(inquiryType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/inquiries/pending")
    public ResponseEntity<List<Inquiry>> getPendingInquiries() {
        return ResponseEntity.ok(inquiryService.getPendingInquiries());
    }
}
