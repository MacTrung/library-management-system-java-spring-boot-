package com.library.entity;

import jakarta.persistence.*;
import lombok.*;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
