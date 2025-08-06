package com.library.service;

import com.library.entity.ExtensionRequest;
import com.library.entity.ExtensionStatus;
import com.library.entity.User;
import com.library.repository.ExtensionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ExtensionRequestService {
    
    @Autowired
    private ExtensionRequestRepository extensionRequestRepository;
    
    public Page<ExtensionRequest> findExtensionRequests(String keyword, ExtensionStatus status, 
                                                       User borrower, LocalDateTime fromDate, 
                                                       LocalDateTime toDate, Pageable pageable) {
        return extensionRequestRepository.findByKeywordAndFilters(keyword, status, borrower, fromDate, toDate, pageable);
    }
    
    public List<ExtensionRequest> findByBorrower(User borrower) {
        return extensionRequestRepository.findByBorrower(borrower);
    }
    
    public Optional<ExtensionRequest> findById(Long id) {
        return extensionRequestRepository.findById(id);
    }
    
    public ExtensionRequest createExtensionRequest(ExtensionRequest request) {
        request.setRequestCode("ER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return extensionRequestRepository.save(request);
    }
    
    public ExtensionRequest updateExtensionRequest(ExtensionRequest request) {
        request.setUpdatedBy(getCurrentUsername());
        return extensionRequestRepository.save(request);
    }
    
    public void cancelExtensionRequest(Long id) {
        ExtensionRequest request = extensionRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Extension request not found"));
        
        if (!request.canBeCancelled()) {
            throw new RuntimeException("Cannot cancel this request");
        }
        
        request.setStatus(ExtensionStatus.CANCELLED);
        request.setUpdatedBy(getCurrentUsername());
        extensionRequestRepository.save(request);
    }
    
    public void processExtensionRequest(Long id, ExtensionStatus status) {
        ExtensionRequest request = extensionRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Extension request not found"));
        
        request.setStatus(status);
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy(getCurrentUsername());
        request.setUpdatedBy(getCurrentUsername());
        
        extensionRequestRepository.save(request);
    }
    
    public void processMultipleRequests(List<Long> requestIds, ExtensionStatus status) {
        for (Long id : requestIds) {
            processExtensionRequest(id, status);
        }
    }
    
    public long countPendingRequests() {
        return extensionRequestRepository.countByStatusIn(
            Arrays.asList(ExtensionStatus.CREATED, ExtensionStatus.PROCESSING)
        );
    }
    
    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }

    public int countByUser(User user) {
        return extensionRequestRepository.countByBorrowRecord_Borrower(user);
    }



}
