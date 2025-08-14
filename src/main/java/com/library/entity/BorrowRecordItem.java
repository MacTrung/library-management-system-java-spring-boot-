package com.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "borrow_record_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecordItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_record_id", nullable = false)
    private BorrowRecord borrowRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private LocalDate expectedReturnDate;
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    private ReturnStatus returnStatus = ReturnStatus.NOT_RETURNED;


    public boolean isOverdue() {
        return returnDate == null && 
               expectedReturnDate != null && 
               expectedReturnDate.isBefore(LocalDate.now());
    }

    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return LocalDate.now().toEpochDay() - expectedReturnDate.toEpochDay();
    }
}

