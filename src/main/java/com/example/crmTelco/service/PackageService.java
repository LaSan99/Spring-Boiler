package com.example.crmTelco.service;

import com.example.crmTelco.entity.Package;
import com.example.crmTelco.entity.User;
import com.example.crmTelco.repository.PackageRepository;
import com.example.crmTelco.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PackageService {

    private final PackageRepository packageRepository;
    private final UserRepository userRepository;

    public PackageService(PackageRepository packageRepository, UserRepository userRepository) {
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
    }

    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    public Optional<Package> getPackageById(Long id) {
        return packageRepository.findById(id);
    }

    public Optional<Package> getPackageByName(String name) {
        return packageRepository.findByName(name);
    }

    public Package createPackage(Package packageEntity) {
        if (packageRepository.existsByName(packageEntity.getName())) {
            throw new RuntimeException("Package name already exists: " + packageEntity.getName());
        }
        
        packageEntity.setActive(true);
        return packageRepository.save(packageEntity);
    }

    public Package updatePackage(Long id, Package packageDetails) {
        Package packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
        
        if (!packageEntity.getName().equals(packageDetails.getName()) && 
            packageRepository.existsByName(packageDetails.getName())) {
            throw new RuntimeException("Package name already exists: " + packageDetails.getName());
        }
        
        packageEntity.setName(packageDetails.getName());
        packageEntity.setDescription(packageDetails.getDescription());
        packageEntity.setPrice(packageDetails.getPrice());
        packageEntity.setDataLimitGB(packageDetails.getDataLimitGB());
        packageEntity.setVoiceMinutes(packageDetails.getVoiceMinutes());
        packageEntity.setSmsCount(packageDetails.getSmsCount());
        
        return packageRepository.save(packageEntity);
    }

    public Package togglePackageStatus(Long id) {
        Package packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
        
        packageEntity.setActive(!packageEntity.isActive());
        return packageRepository.save(packageEntity);
    }

    public void deletePackage(Long id) {
        if (!packageRepository.existsById(id)) {
            throw new RuntimeException("Package not found with id: " + id);
        }
        packageRepository.deleteById(id);
    }

    public long getTotalPackagesCount() {
        return packageRepository.count();
    }

    public long getActivePackagesCount() {
        return packageRepository.findByActive(true).size();
    }

    public List<Package> getActivePackages() {
        return packageRepository.findByActive(true);
    }

    public List<Package> getInactivePackages() {
        return packageRepository.findByActive(false);
    }

    public List<Package> getPackagesByPriceRange(double minPrice, double maxPrice) {
        return packageRepository.findAll().stream()
                .filter(pkg -> pkg.getPrice().doubleValue() >= minPrice && 
                              pkg.getPrice().doubleValue() <= maxPrice)
                .toList();
    }

    public List<Package> getPackagesByDataLimit(int minDataGB) {
        return packageRepository.findAll().stream()
                .filter(pkg -> pkg.getDataLimitGB() >= minDataGB)
                .toList();
    }

    // User-specific package methods
    public List<Package> getUserPackages(Long userId) {
        return packageRepository.findByUserId(userId);
    }

    public List<Package> getUserActivePackages(Long userId) {
        return packageRepository.findByUserIdAndActive(userId, true);
    }

    public List<Package> getUserPackagesByType(Long userId, Package.PackageType packageType) {
        return packageRepository.findByUserIdAndPackageType(userId, packageType);
    }

    public Package createPackageForUser(Long userId, Package packageEntity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Validate package type matches user category
        if (packageEntity.getPackageType() != null && user.getCategory() != null) {
            if ((packageEntity.getPackageType() == Package.PackageType.PREPAID && user.getCategory() != User.Category.PREPAID) ||
                (packageEntity.getPackageType() == Package.PackageType.POSTPAID && user.getCategory() != User.Category.POSTPAID)) {
                throw new RuntimeException("Package type must match user category. User is " + user.getCategory() + 
                                         " but package is " + packageEntity.getPackageType());
            }
        }
        
        packageEntity.setUser(user);
        packageEntity.setActive(true);
        return packageRepository.save(packageEntity);
    }

    public Package createPackageForUserWithValidation(Long userId, Package packageEntity) {
        // Generate unique package name if not provided
        if (packageEntity.getName() == null || packageEntity.getName().trim().isEmpty()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            packageEntity.setName(user.getUsername() + "_" + packageEntity.getPackageType() + "_" + System.currentTimeMillis());
        }
        
        return createPackageForUser(userId, packageEntity);
    }

    public Package updatePaymentStatus(Long packageId, Package.PaymentStatus status) {
        Package packageEntity = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + packageId));
        
        packageEntity.setPaymentStatus(status);
        return packageRepository.save(packageEntity);
    }
}
