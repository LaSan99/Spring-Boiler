package com.example.crmTelco.controller;

import com.example.crmTelco.entity.Package;
import com.example.crmTelco.entity.User;
import com.example.crmTelco.service.PackageService;
import com.example.crmTelco.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserPackageController {

    private final UserService userService;
    private final PackageService packageService;

    public UserPackageController(UserService userService, PackageService packageService) {
        this.userService = userService;
        this.packageService = packageService;
    }

    @GetMapping("/{userId}/packages")
    public ResponseEntity<List<Package>> getUserPackages(@PathVariable Long userId) {
        // Verify user exists
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Package> packages = packageService.getUserPackages(userId);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/{userId}/packages/active")
    public ResponseEntity<List<Package>> getUserActivePackages(@PathVariable Long userId) {
        // Verify user exists
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Package> packages = packageService.getUserActivePackages(userId);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/{userId}/packages/type/{packageType}")
    public ResponseEntity<List<Package>> getUserPackagesByType(@PathVariable Long userId, 
                                                              @PathVariable String packageType) {
        // Verify user exists
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Package.PackageType type = Package.PackageType.valueOf(packageType.toUpperCase());
            List<Package> packages = packageService.getUserPackagesByType(userId, type);
            return ResponseEntity.ok(packages);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid package type. Must be PREPAID or POSTPAID");
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{userId}/packages")
    public ResponseEntity<?> createPackageForUser(@PathVariable Long userId, 
                                                   @RequestBody Package packageEntity) {
        try {
            // Verify user exists
            if (!userService.getUserById(userId).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found with id: " + userId);
                return ResponseEntity.notFound().build();
            }
            
            Package createdPackage = packageService.createPackageForUserWithValidation(userId, packageEntity);
            return ResponseEntity.ok(createdPackage);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{userId}/package-summary")
    public ResponseEntity<Map<String, Object>> getUserPackageSummary(@PathVariable Long userId) {
        // Verify user exists
        if (!userService.getUserById(userId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Package> allPackages = packageService.getUserPackages(userId);
        List<Package> activePackages = packageService.getUserActivePackages(userId);
        List<Package> prepaidPackages = packageService.getUserPackagesByType(userId, Package.PackageType.PREPAID);
        List<Package> postpaidPackages = packageService.getUserPackagesByType(userId, Package.PackageType.POSTPAID);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPackages", allPackages.size());
        summary.put("activePackages", activePackages.size());
        summary.put("prepaidPackages", prepaidPackages.size());
        summary.put("postpaidPackages", postpaidPackages.size());
        summary.put("packages", allPackages);
        
        return ResponseEntity.ok(summary);
    }
}
