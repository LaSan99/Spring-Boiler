package com.example.crmTelco.controller;

import com.example.crmTelco.entity.Package;
import com.example.crmTelco.entity.User;
import com.example.crmTelco.service.PackageService;
import com.example.crmTelco.service.UserService;
import com.example.crmTelco.service.InvoiceService;
import com.example.crmTelco.dto.PackageCreationRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserPackageController {

    private final UserService userService;
    private final PackageService packageService;
    private final InvoiceService invoiceService;

    public UserPackageController(UserService userService, PackageService packageService, 
                                InvoiceService invoiceService) {
        this.userService = userService;
        this.packageService = packageService;
        this.invoiceService = invoiceService;
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

    @PostMapping("/{userId}/packages-with-invoice")
    public ResponseEntity<?> createPackageWithInvoice(@PathVariable Long userId, 
                                                       @RequestBody PackageCreationRequest request) {
        try {
            // Verify user exists
            User user = userService.getUserById(userId).orElse(null);
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found with id: " + userId);
                return ResponseEntity.notFound().build();
            }
            
            // Create package from request
            Package packageEntity = new Package();
            packageEntity.setName(request.getName());
            packageEntity.setDescription(request.getDescription());
            packageEntity.setPrice(request.getPrice());
            packageEntity.setDataLimitGB(request.getDataLimitGB());
            packageEntity.setVoiceMinutes(request.getVoiceMinutes());
            packageEntity.setSmsCount(request.getSmsCount());
            packageEntity.setPackageType(request.getPackageType());
            packageEntity.setPaymentStatus(request.getPaymentStatus());
            packageEntity.setUser(user);
            
            // Save package
            Package createdPackage = packageService.createPackageForUserWithValidation(userId, packageEntity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("package", createdPackage);
            response.put("message", "Package created successfully");
            response.put("invoiceDownloadUrl", "/api/admin/users/" + userId + "/packages/" + createdPackage.getId() + "/invoice");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{userId}/packages/{packageId}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long userId, 
                                                   @PathVariable Long packageId) {
        try {
            // Verify user exists
            User user = userService.getUserById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Get package
            Package packageEntity = packageService.getPackageById(packageId).orElse(null);
            if (packageEntity == null || !packageEntity.getUser().getId().equals(userId)) {
                return ResponseEntity.notFound().build();
            }
            
            // Generate PDF invoice
            byte[] pdfInvoice = invoiceService.generateInvoicePdf(packageEntity, user);
            
            // Debug: Check if PDF was generated
            if (pdfInvoice == null || pdfInvoice.length == 0) {
                System.err.println("PDF generation failed - empty or null result");
                return ResponseEntity.internalServerError().build();
            }
            
            System.out.println("Downloading PDF with size: " + pdfInvoice.length + " bytes");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Invoice_" + packageEntity.getInvoiceNumber() + ".pdf");
            headers.setContentLength(pdfInvoice.length);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfInvoice);
            
        } catch (IOException e) {
            System.err.println("Error generating invoice: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{userId}/packages/{packageId}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long userId, 
                                                  @PathVariable Long packageId,
                                                  @RequestBody Map<String, String> request) {
        try {
            // Verify user exists
            if (!userService.getUserById(userId).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found with id: " + userId);
                return ResponseEntity.notFound().build();
            }
            
            String statusStr = request.get("status");
            if (statusStr == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Payment status is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            Package.PaymentStatus status;
            try {
                status = Package.PaymentStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid payment status. Must be PENDING or PAID");
                return ResponseEntity.badRequest().body(error);
            }
            
            Package updatedPackage = packageService.updatePaymentStatus(packageId, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("package", updatedPackage);
            response.put("message", "Payment status updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
