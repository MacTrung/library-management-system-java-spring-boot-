package com.library.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "borrow_record_items")
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

    // Constructors
    public BorrowRecordItem() {}

    public BorrowRecordItem(BorrowRecord borrowRecord, Book book, LocalDate expectedReturnDate) {
        this.borrowRecord = borrowRecord;
        this.book = book;
        this.expectedReturnDate = expectedReturnDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BorrowRecord getBorrowRecord() { return borrowRecord; }
    public void setBorrowRecord(BorrowRecord borrowRecord) { this.borrowRecord = borrowRecord; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public ReturnStatus getReturnStatus() { return returnStatus; }
    public void setReturnStatus(ReturnStatus returnStatus) { this.returnStatus = returnStatus; }

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

