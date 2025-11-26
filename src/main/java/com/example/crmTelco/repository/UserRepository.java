package com.example.crmTelco.repository;

import com.example.crmTelco.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByMsisdn(String msisdn);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByMsisdn(String msisdn);
    List<User> findByCategory(User.Category category);
    List<User> findByRoleAndCategory(User.Role role, User.Category category);
}
