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

import java.util.List;
import java.util.Optional;

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
    
    public Book createBook(Book book) {
        book.setCreatedBy(getCurrentUsername());
        return bookRepository.save(book);
    }
    
    public Book updateBook(Book book) {
        book.setUpdatedBy(getCurrentUsername());
        return bookRepository.save(book);
    }
    
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
    
    public long countTotalBooks() {
        return bookRepository.countTotalBooks();
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
}
