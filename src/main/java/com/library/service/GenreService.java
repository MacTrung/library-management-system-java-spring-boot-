package com.library.service;

import com.library.entity.Genre;
import com.library.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GenreService {
    
    @Autowired
    private GenreRepository genreRepository;
    
    public List<Genre> findAllGenres() {
        return genreRepository.findAll();
    }
    
    public Optional<Genre> findById(Long id) {
        return genreRepository.findById(id);
    }
    
    public Genre createGenre(Genre genre) {
        genre.setCreatedBy(getCurrentUsername());
        return genreRepository.save(genre);
    }
    
    public Genre updateGenre(Genre genre) {
        genre.setUpdatedBy(getCurrentUsername());
        return genreRepository.save(genre);
    }
    
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
    
    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }
}
