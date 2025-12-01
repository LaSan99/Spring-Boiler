package com.example.crmTelco.service;

import com.example.crmTelco.entity.Inquiry;
import com.example.crmTelco.repository.InquiryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public InquiryService(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAllOrderByCreatedAtDesc();
    }

    public Optional<Inquiry> getInquiryById(Long id) {
        return inquiryRepository.findById(id);
    }

    public Inquiry createInquiry(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    public Inquiry updateInquiry(Long id, Inquiry inquiryDetails) {
        return inquiryRepository.findById(id)
                .map(inquiry -> {
                    inquiry.setSubject(inquiryDetails.getSubject());
                    inquiry.setMessage(inquiryDetails.getMessage());
                    inquiry.setType(inquiryDetails.getType());
                    inquiry.setStatus(inquiryDetails.getStatus());
                    return inquiryRepository.save(inquiry);
                })
                .orElseThrow(() -> new RuntimeException("Inquiry not found with id: " + id));
    }

    public Inquiry updateInquiryStatus(Long id, String status) {
        return inquiryRepository.findById(id)
                .map(inquiry -> {
                    inquiry.setStatus(Inquiry.InquiryStatus.valueOf(status));
                    return inquiryRepository.save(inquiry);
                })
                .orElseThrow(() -> new RuntimeException("Inquiry not found with id: " + id));
    }

    public Inquiry respondToInquiry(Long id, String response) {
        return inquiryRepository.findById(id)
                .map(inquiry -> {
                    inquiry.setAdminResponse(response);
                    inquiry.setStatus(Inquiry.InquiryStatus.RESOLVED);
                    return inquiryRepository.save(inquiry);
                })
                .orElseThrow(() -> new RuntimeException("Inquiry not found with id: " + id));
    }

    public void deleteInquiry(Long id) {
        if (!inquiryRepository.existsById(id)) {
            throw new RuntimeException("Inquiry not found with id: " + id);
        }
        inquiryRepository.deleteById(id);
    }

    public List<Inquiry> getInquiriesByUserId(Long userId) {
        return inquiryRepository.findByUserId(userId);
    }

    public List<Inquiry> getInquiriesByStatus(Inquiry.InquiryStatus status) {
        return inquiryRepository.findByStatus(status);
    }

    public List<Inquiry> getInquiriesByType(Inquiry.InquiryType type) {
        return inquiryRepository.findByType(type);
    }

    public List<Inquiry> getPendingInquiries() {
        return inquiryRepository.findPendingInquiriesOrderByCreatedAtAsc();
    }

    public long getTotalInquiriesCount() {
        return inquiryRepository.count();
    }

    public long getInquiriesCountByStatus(Inquiry.InquiryStatus status) {
        return inquiryRepository.countByStatus(status);
    }
}
