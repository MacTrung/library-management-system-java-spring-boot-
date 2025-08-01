package com.library.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "borrow_records")
@EntityListeners(AuditingEntityListener.class)
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String borrowCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @OneToMany(mappedBy = "borrowRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowRecordItem> items = new ArrayList<>();

    private LocalDate borrowDate;
    private BigDecimal deposit;

    @OneToMany(mappedBy = "borrowRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExtensionRequest> extensionRequests = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    // Constructors
    public BorrowRecord() {}

    public BorrowRecord(String borrowCode, User borrower, LocalDate borrowDate) {
        this.borrowCode = borrowCode;
        this.borrower = borrower;
        this.borrowDate = borrowDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBorrowCode() { return borrowCode; }
    public void setBorrowCode(String borrowCode) { this.borrowCode = borrowCode; }

    public User getBorrower() { return borrower; }
    public void setBorrower(User borrower) { this.borrower = borrower; }

    public List<BorrowRecordItem> getItems() { return items; }
    public void setItems(List<BorrowRecordItem> items) { this.items = items; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public BigDecimal getDeposit() { return deposit; }
    public void setDeposit(BigDecimal deposit) { this.deposit = deposit; }

    public List<ExtensionRequest> getExtensionRequests() { return extensionRequests; }
    public void setExtensionRequests(List<ExtensionRequest> extensionRequests) { this.extensionRequests = extensionRequests; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public boolean hasOverdueBooks() {
        if (items == null) return false;
        return items.stream().anyMatch(item -> 
            item.getReturnDate() == null && 
            item.getExpectedReturnDate() != null &&
            item.getExpectedReturnDate().isBefore(LocalDate.now())
        );
    }

    public long getOverdueBooksCount() {
        if (items == null) return 0;
        return items.stream().mapToLong(item -> 
            (item.getReturnDate() == null && 
             item.getExpectedReturnDate() != null &&
             item.getExpectedReturnDate().isBefore(LocalDate.now())) ? 1 : 0
        ).sum();
    }

    public boolean isFullyReturned() {
        if (items == null || items.isEmpty()) return true;
        return items.stream().allMatch(item -> item.getReturnDate() != null);
    }
}
