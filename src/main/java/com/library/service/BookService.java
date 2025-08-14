package com.library.service;

import com.library.entity.Book;
import com.library.entity.BookCondition;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public Page<Book> findBooks(String keyword, BookCondition condition, Boolean canBorrow, Pageable pageable) {
        return bookRepository.findByKeywordAndFilters(keyword, condition, canBorrow, pageable);
    }
    
    public Page<Book> findAvailableBooks(Pageable pageable) {
        return bookRepository.findAvailableBooks(pageable);
    }

    public List<Book> findAllAvailableBooks() {
        return bookRepository.findAvailableBooks(Pageable.unpaged()).getContent();
    }



    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
    

    
    public Book updateBook(Book book) {
        String username = getCurrentUsername(); //không có thì tạo mới luôn
        if (book.getId() == null) {
            book.setCreatedBy(username);
        }
        book.setUpdatedBy(username);
        return bookRepository.save(book);
    }
    
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
    
    public long countTotalBooks() {
        return bookRepository.countBy();
    }
    
    public long countBorrowedBooks() {
        return bookRepository.countBorrowedBooks();
    }
    
    public long countOverdueBooks() {
        return bookRepository.countOverdueBooks();
    }
    
    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }

    public Map<String, Object> getGenreDistribution() {
        List<Object[]> rawStats = bookRepository.countBooksByGenre();
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        for (Object[] row : rawStats) {
            labels.add((String) row[0]);
            values.add(((Number) row[1]).intValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("values", values);
        return result;
    }

}
