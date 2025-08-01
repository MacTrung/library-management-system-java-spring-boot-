package com.library.repository;

import com.library.entity.Bookshelf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {
    
    @Query("SELECT bs FROM Bookshelf bs WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(bs.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(bs.section) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:floor IS NULL OR bs.floor = :floor)")
    Page<Bookshelf> findByKeywordAndFloor(@Param("keyword") String keyword,
                                         @Param("floor") Integer floor,
                                         Pageable pageable);
}
