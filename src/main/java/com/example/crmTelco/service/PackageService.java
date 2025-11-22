package com.example.crmTelco.service;

import com.example.crmTelco.entity.Package;
import com.example.crmTelco.repository.PackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PackageService {

    private final PackageRepository packageRepository;

    public PackageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
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
}
