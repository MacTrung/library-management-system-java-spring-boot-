package com.library.service;

import com.library.entity.Author;
import com.library.repository.AuthorRepository;
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
public class AuthorService {
    
    @Autowired
    private AuthorRepository authorRepository;
    
    public Page<Author> findAuthors(String keyword, Integer birthYear, Integer deathYear, Pageable pageable) {
        return authorRepository.findByKeywordAndFilters(keyword, birthYear, deathYear, pageable);

    }
    
    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }
    
    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }
    
    public Author createAuthor(Author author) {
        author.setCreatedBy(getCurrentUsername());
        return authorRepository.save(author);
    }
    
    public Author updateAuthor(Author author) {
        author.setUpdatedBy(getCurrentUsername());
        return authorRepository.save(author);
    }
    
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }
    
    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }
}
