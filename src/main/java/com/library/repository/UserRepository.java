package com.library.repository;

import com.library.entity.User;
import com.library.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    @Query("""
           SELECT u 
           FROM User u
           WHERE 
               (:keyword IS NULL OR 
                LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))
               AND (:status IS NULL OR u.status = :status)
           """)
    Page<User> findByKeywordAndStatus(@Param("keyword") String keyword, 
                                     @Param("status") UserStatus status, 
                                     Pageable pageable);
}
