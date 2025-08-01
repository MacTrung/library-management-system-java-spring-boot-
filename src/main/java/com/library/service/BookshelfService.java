package com.library.service;

import com.library.entity.Bookshelf;
import com.library.repository.BookshelfRepository;
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
public class BookshelfService {
    
    @Autowired
    private BookshelfRepository bookshelfRepository;
    
    public Page<Bookshelf> findBookshelves(String keyword, Integer floor, Pageable pageable) {
        return bookshelfRepository.findByKeywordAndFloor(keyword, floor, pageable);
    }
    
    public List<Bookshelf> findAllBookshelves() {
        return bookshelfRepository.findAll();
    }
    
    public Optional<Bookshelf> findById(Long id) {
        return bookshelfRepository.findById(id);
    }
    
    public Bookshelf createBookshelf(Bookshelf bookshelf) {
        bookshelf.setCreatedBy(getCurrentUsername());
        return bookshelfRepository.save(bookshelf);
    }
    
    public Bookshelf updateBookshelf(Bookshelf bookshelf) {
        bookshelf.setUpdatedBy(getCurrentUsername());
        return bookshelfRepository.save(bookshelf);
    }
    
    public void deleteBookshelf(Long id) {
        Bookshelf bookshelf = bookshelfRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Bookshelf not found"));
        
        if (!bookshelf.canBeDeleted()) {
            throw new RuntimeException("Cannot delete bookshelf with books");
        }
        
        bookshelfRepository.deleteById(id);
    }
    
    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }
}
