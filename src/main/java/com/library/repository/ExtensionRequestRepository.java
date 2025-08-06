package com.library.repository;

import com.library.entity.ExtensionRequest;
import com.library.entity.ExtensionStatus;
import com.library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExtensionRequestRepository extends JpaRepository<ExtensionRequest, Long> {
    
    @Query("SELECT DISTINCT er FROM ExtensionRequest er " +
           "LEFT JOIN er.borrowRecord br " +
           "LEFT JOIN er.book b " +
           "WHERE (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(er.requestCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(br.borrowCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR er.status = :status) AND " +
           "(:borrower IS NULL OR br.borrower = :borrower) AND " +
           "(:fromDate IS NULL OR er.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR er.createdAt <= :toDate)")
    Page<ExtensionRequest> findByKeywordAndFilters(@Param("keyword") String keyword,
                                                  @Param("status") ExtensionStatus status,
                                                  @Param("borrower") User borrower,
                                                  @Param("fromDate") LocalDateTime fromDate,
                                                  @Param("toDate") LocalDateTime toDate,
                                                  Pageable pageable);

    @Query("SELECT er FROM ExtensionRequest er WHERE er.borrowRecord.borrower = :borrower")
    List<ExtensionRequest> findByBorrower(@Param("borrower") User borrower);

    @Query("SELECT COUNT(er) FROM ExtensionRequest er WHERE er.status IN :statuses")
    long countByStatusIn(@Param("statuses") List<ExtensionStatus> statuses);

    int countByBorrowRecord_Borrower(User borrower);

}
