package com.library.repository;

import com.library.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    @Query("SELECT a FROM Author a WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.biography) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:birthYear IS NULL OR a.birthYear = :birthYear) AND " +
           "(:deathYear IS NULL OR a.deathYear = :deathYear)")
    Page<Author> findByKeywordAndFilters(@Param("keyword") String keyword,
                                        @Param("birthYear") Integer birthYear,
                                        @Param("deathYear") Integer deathYear,
                                        Pageable pageable);
}
