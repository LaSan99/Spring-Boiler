package com.example.crmTelco.repository;

import com.example.crmTelco.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    Optional<Package> findByName(String name);
    List<Package> findByActive(boolean active);
    boolean existsByName(String name);
}
