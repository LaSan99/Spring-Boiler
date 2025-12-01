package com.example.crmTelco.repository;

import com.example.crmTelco.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByUserId(Long userId);

    List<Inquiry> findByStatus(Inquiry.InquiryStatus status);

    List<Inquiry> findByType(Inquiry.InquiryType type);

    @Query("SELECT i FROM Inquiry i WHERE i.user.id = :userId AND i.status = :status")
    List<Inquiry> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Inquiry.InquiryStatus status);

    @Query("SELECT i FROM Inquiry i ORDER BY i.createdAt DESC")
    List<Inquiry> findAllOrderByCreatedAtDesc();

    @Query("SELECT i FROM Inquiry i WHERE i.status = 'PENDING' ORDER BY i.createdAt ASC")
    List<Inquiry> findPendingInquiriesOrderByCreatedAtAsc();

    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.status = :status")
    long countByStatus(@Param("status") Inquiry.InquiryStatus status);
}
