package com.example.crmTelco.controller;

import com.example.crmTelco.entity.User;
import com.example.crmTelco.entity.Package;
import com.example.crmTelco.repository.UserRepository;
import com.example.crmTelco.repository.PackageRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final PackageRepository packageRepository;

    public AdminController(UserRepository userRepository, PackageRepository packageRepository) {
        this.userRepository = userRepository;
        this.packageRepository = packageRepository;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        long totalUsers = userRepository.count();
        long totalPackages = packageRepository.count();
        long activePackages = packageRepository.findByActive(true).size();
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalPackages", totalPackages);
        model.addAttribute("activePackages", activePackages);
        model.addAttribute("currentUser", authentication.getName());
        
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String users(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/admin/packages")
    public String packages(Model model) {
        List<Package> packages = packageRepository.findAll();
        model.addAttribute("packages", packages);
        return "admin/packages";
    }

    @PostMapping("/admin/users/create")
    public String createUser(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           @RequestParam String fullName,
                           @RequestParam String role,
                           @RequestParam(required = false) String address,
                           @RequestParam(required = false) String msisdn,
                           @RequestParam String category,
                           @RequestParam(required = false) String contactDetails,
                           RedirectAttributes redirectAttributes) {
        
        if (userRepository.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/admin/users";
        }
        
        if (userRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email already exists");
            return "redirect:/admin/users";
        }
        
        if (msisdn != null && userRepository.existsByMsisdn(msisdn)) {
            redirectAttributes.addFlashAttribute("error", "MSISDN already exists");
            return "redirect:/admin/users";
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // Note: In production, encode this password
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(User.Role.valueOf(role.toUpperCase()));
        user.setEnabled(true);
        user.setAddress(address);
        user.setMsisdn(msisdn);
        user.setCategory(User.Category.valueOf(category.toUpperCase()));
        user.setContactDetails(contactDetails);
        
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "User created successfully");
        
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/packages/create")
    public String createPackage(@RequestParam String name,
                               @RequestParam String description,
                               @RequestParam String price,
                               @RequestParam Integer dataLimitGB,
                               @RequestParam Integer voiceMinutes,
                               @RequestParam Integer smsCount,
                               RedirectAttributes redirectAttributes) {
        
        if (packageRepository.existsByName(name)) {
            redirectAttributes.addFlashAttribute("error", "Package name already exists");
            return "redirect:/admin/packages";
        }
        
        Package packageEntity = new Package();
        packageEntity.setName(name);
        packageEntity.setDescription(description);
        packageEntity.setPrice(java.math.BigDecimal.valueOf(Double.parseDouble(price)));
        packageEntity.setDataLimitGB(dataLimitGB);
        packageEntity.setVoiceMinutes(voiceMinutes);
        packageEntity.setSmsCount(smsCount);
        packageEntity.setActive(true);
        
        packageRepository.save(packageEntity);
        redirectAttributes.addFlashAttribute("success", "Package created successfully");
        
        return "redirect:/admin/packages";
    }

    @PostMapping("/admin/users/toggle-status")
    public String toggleUserStatus(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "User status updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/packages/toggle-status")
    public String togglePackageStatus(@RequestParam Long packageId, RedirectAttributes redirectAttributes) {
        Optional<Package> packageOpt = packageRepository.findById(packageId);
        if (packageOpt.isPresent()) {
            Package packageEntity = packageOpt.get();
            packageEntity.setActive(!packageEntity.isActive());
            packageRepository.save(packageEntity);
            redirectAttributes.addFlashAttribute("success", "Package status updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Package not found");
        }
        return "redirect:/admin/packages";
    }
}
