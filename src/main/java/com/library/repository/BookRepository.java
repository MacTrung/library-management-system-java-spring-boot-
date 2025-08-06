package com.library.repository;

import com.library.entity.Book;
import com.library.entity.BookCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN b.authors a " +
           "LEFT JOIN b.genres g " +
           "LEFT JOIN b.bookshelf bs " +
           "WHERE (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(bs.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:condition IS NULL OR b.condition = :condition) AND " +
           "(:canBorrow IS NULL OR b.canBorrow = :canBorrow)")
    Page<Book> findByKeywordAndFilters(@Param("keyword") String keyword,
                                      @Param("condition") BookCondition condition,
                                      @Param("canBorrow") Boolean canBorrow,
                                      Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.canBorrow = true AND " +
           "NOT EXISTS (SELECT 1 FROM BorrowRecordItem bri WHERE bri.book = b AND bri.returnDate IS NULL)")
    Page<Book> findAvailableBooks(Pageable pageable);

    @Query("SELECT COUNT(b) FROM Book b")
    long countTotalBooks();

    @Query("SELECT COUNT(DISTINCT bri.book) FROM BorrowRecordItem bri WHERE bri.returnDate IS NULL")
    long countBorrowedBooks();

    @Query("SELECT COUNT(DISTINCT bri.book) FROM BorrowRecordItem bri WHERE bri.returnDate IS NULL AND bri.expectedReturnDate < CURRENT_DATE")
    long countOverdueBooks();

    @Query("SELECT g.name, COUNT(b.id) " +
            "FROM Book b JOIN b.genres g " +
            "GROUP BY g.name ORDER BY COUNT(b.id) DESC")
    List<Object[]> countBooksByGenre();

}
