package com.library.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "extension_requests")
@EntityListeners(AuditingEntityListener.class)
public class ExtensionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String requestCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_record_id", nullable = false)
    private BorrowRecord borrowRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    private ExtensionStatus status = ExtensionStatus.CREATED;

    private LocalDateTime processedAt;
    private String processedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String updatedBy;

    // Constructors
    public ExtensionRequest() {}

    public ExtensionRequest(String requestCode, BorrowRecord borrowRecord, Book book, String reason) {
        this.requestCode = requestCode;
        this.borrowRecord = borrowRecord;
        this.book = book;
        this.reason = reason;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRequestCode() { return requestCode; }
    public void setRequestCode(String requestCode) { this.requestCode = requestCode; }

    public BorrowRecord getBorrowRecord() { return borrowRecord; }
    public void setBorrowRecord(BorrowRecord borrowRecord) { this.borrowRecord = borrowRecord; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public ExtensionStatus getStatus() { return status; }
    public void setStatus(ExtensionStatus status) { this.status = status; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public boolean canBeCancelled() {
        return status == ExtensionStatus.CREATED || status == ExtensionStatus.PROCESSING;
    }
}

