package com.library.service;

import com.library.entity.*;
import com.library.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BorrowService {
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Autowired
    private BookService bookService;
    
    public Page<BorrowRecord> findBorrowRecords(String keyword, User borrower, 
                                               LocalDate fromDate, LocalDate toDate, 
                                               Pageable pageable) {
        return borrowRecordRepository.findByKeywordAndFilters(keyword, borrower, fromDate, toDate, pageable);
    }
    
    public Optional<BorrowRecord> findById(Long id) {
        return borrowRecordRepository.findById(id);
    }
    
    public List<BorrowRecord> findByBorrower(User borrower) {
        return borrowRecordRepository.findByBorrower(borrower);
    }
    
    public List<BorrowRecord> findByBorrowerAndDateRange(User borrower, LocalDate fromDate) {
        return borrowRecordRepository.findByBorrowerAndDateRange(borrower, fromDate);
    }
    
    public List<BorrowRecord> findOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords(LocalDate.now());
    }
    
//    public BorrowRecord createBorrowRecord(BorrowRecord borrowRecord) {
//        borrowRecord.setBorrowCode("BR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
//        borrowRecord.setCreatedBy(getCurrentUsername());
//        return borrowRecordRepository.save(borrowRecord);
//    }
    public BorrowRecord createBorrowRecord(BorrowRecord borrowRecord) {
        borrowRecord.setBorrowCode("BR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        borrowRecord.setCreatedBy(getCurrentUsername());

        for (BorrowRecordItem item : borrowRecord.getItems()) {
            Book book = item.getBook();
            if (book != null && book.getId() != null) {
                Book fullBook = bookService.findById(book.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + book.getId()));

                fullBook.setCanBorrow(false);
                bookService.updateBook(fullBook);

                item.setBook(fullBook);
            }
        }

        return borrowRecordRepository.save(borrowRecord);
    }
    
    public BorrowRecord updateBorrowRecord(BorrowRecord borrowRecord) {
        borrowRecord.setUpdatedBy(getCurrentUsername());
        return borrowRecordRepository.save(borrowRecord);
    }
    
    public void deleteBorrowRecord(Long id) {
        borrowRecordRepository.deleteById(id);
    }
    
//    public boolean canUserBorrow(User user) {
//        if (!user.isActive()) return false;
//
//        List<BorrowRecord> userRecords = findByBorrower(user);
//        return userRecords.stream().noneMatch(BorrowRecord::hasOverdueBooks);
//    }
    
    public void returnBook(Long borrowRecordId, Long bookId) {
        BorrowRecord record = borrowRecordRepository.findById(borrowRecordId)
            .orElseThrow(() -> new RuntimeException("Borrow record not found"));
        
        record.getItems().stream()
            .filter(item -> item.getBook().getId().equals(bookId))
            .findFirst()
            .ifPresent(item -> {
                item.setReturnDate(LocalDate.now());
                item.setReturnStatus(item.getReturnDate().isAfter(item.getExpectedReturnDate()) ? 
                                   ReturnStatus.RETURNED_LATE : ReturnStatus.RETURNED_ON_TIME);
            });
        
        record.setUpdatedBy(getCurrentUsername());
        borrowRecordRepository.save(record);
    }
    
    public long countBorrowRecordsByDateRange(LocalDate fromDate, LocalDate toDate) {
        return borrowRecordRepository.countBorrowRecordsByDateRange(fromDate, toDate);
    }
    
    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }


    public boolean canBorrowBook(Book book) {
        return book != null && Boolean.TRUE.equals(book.getCanBorrow()) && book.isAvailable();
    }

    public boolean hasDueSoonBooks(User user, int daysThreshold) {
        List<BorrowRecord> records = findByBorrower(user);
        LocalDate today = LocalDate.now();

        return records.stream()
                .flatMap(r -> r.getItems().stream())
                .anyMatch(item ->
                        item.getReturnDate() == null &&
                                item.getExpectedReturnDate() != null &&
                                !item.getExpectedReturnDate().isBefore(today) &&
                                item.getExpectedReturnDate().minusDays(daysThreshold).isBefore(today)
                );
    }

    public boolean hasUnreturnedBooks(User user) {
        return findByBorrower(user).stream()
                .flatMap(r -> r.getItems().stream())
                .anyMatch(item -> item.getReturnDate() == null);
    }

    public boolean hasOverdueBooks(User user) {
        LocalDate today = LocalDate.now();
        return findByBorrower(user).stream()
                .flatMap(r -> r.getItems().stream())
                .anyMatch(item ->
                        item.getReturnDate() == null &&
                                item.getExpectedReturnDate() != null &&
                                item.getExpectedReturnDate().isBefore(today)
                );
    }

    public boolean canUserBorrow(User user) {
        if (user == null || !user.isActive()) return false;

        return !hasUnreturnedBooks(user) && !hasOverdueBooks(user);
    }

    public boolean isBookCurrentlyBorrowedByUser(User user, Book book) {
        return findByBorrower(user).stream()
                .flatMap(record -> record.getItems().stream())
                .anyMatch(item ->
                        item.getBook().getId().equals(book.getId()) &&
                                item.getReturnDate() == null
                );
    }



}
