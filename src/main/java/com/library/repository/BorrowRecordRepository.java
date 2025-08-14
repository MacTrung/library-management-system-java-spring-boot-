package com.library.repository;

import com.library.entity.BorrowRecord;
import com.library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    
    List<BorrowRecord> findByBorrower(User borrower);
    
    @Query("SELECT DISTINCT br FROM BorrowRecord br " +
           "LEFT JOIN br.items bri " +
           "LEFT JOIN bri.book b " +
           "WHERE (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(br.borrowCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(br.borrower.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(br.borrower.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:borrower IS NULL OR br.borrower = :borrower) AND " +
           "(:fromDate IS NULL OR br.borrowDate >= :fromDate) AND " +
           "(:toDate IS NULL OR br.borrowDate <= :toDate)")
    Page<BorrowRecord> findByKeywordAndFilters(@Param("keyword") String keyword,
                                              @Param("borrower") User borrower,
                                              @Param("fromDate") LocalDate fromDate,
                                              @Param("toDate") LocalDate toDate,
                                              Pageable pageable);
    
    @Query("SELECT br FROM BorrowRecord br " +
           "JOIN br.items bri " +
           "WHERE bri.returnDate IS NULL AND bri.expectedReturnDate < :currentDate")
    List<BorrowRecord> findOverdueRecords(@Param("currentDate") LocalDate currentDate);


//    @Query("SELECT br FROM BorrowRecord br WHERE br.borrower = :borrower AND br.borrowDate >= :fromDate")
//    List<BorrowRecord> findByBorrowerAndDateRange(@Param("borrower") User borrower,
//                                                 @Param("fromDate") LocalDate fromDate);
//    đã bị thay bơởi thg dưới
    List<BorrowRecord> findByBorrowerAndBorrowDateGreaterThanEqual(
            User borrower, LocalDate fromDate
    );


//    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.borrowDate BETWEEN :fromDate AND :toDate")
//    long countBorrowRecordsByDateRange(@Param("fromDate") LocalDate fromDate,
//                                      @Param("toDate") LocalDate toDate);
//      bị thay bởi thg dưới
    long countByBorrowDateBetween(LocalDate fromDate, LocalDate toDate);

    @Query(value = """
        SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS total
        FROM borrow_records
        WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
        GROUP BY month
        ORDER BY month
    """, nativeQuery = true)
    List<Object[]> countByMonthLast6Months();
}
