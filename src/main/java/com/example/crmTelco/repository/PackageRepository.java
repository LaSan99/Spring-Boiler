package com.example.crmTelco.repository;

import com.example.crmTelco.entity.Package;
import com.example.crmTelco.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    Optional<Package> findByName(String name);
    List<Package> findByActive(boolean active);
    boolean existsByName(String name);
    List<Package> findByUser(User user);
    List<Package> findByUserId(Long userId);
    List<Package> findByUserAndActive(User user, boolean active);
    List<Package> findByUserIdAndActive(Long userId, boolean active);
    List<Package> findByPackageType(Package.PackageType packageType);
    List<Package> findByUserAndPackageType(User user, Package.PackageType packageType);
    List<Package> findByUserIdAndPackageType(Long userId, Package.PackageType packageType);
}
