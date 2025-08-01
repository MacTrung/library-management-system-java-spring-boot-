package com.library.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
@EntityListeners(AuditingEntityListener.class)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer publicationYear;
    private Integer edition;

    @Enumerated(EnumType.STRING)
    @Column(name = "`condition`") //cái này là từ
    private BookCondition condition = BookCondition.NEW;

    private Boolean canBorrow = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_genres",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookshelf_id")
    private Bookshelf bookshelf;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowRecordItem> borrowRecordItems;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    // Constructors
    public Book() {}

    public Book(String title, Integer publicationYear) {
        this.title = title;
        this.publicationYear = publicationYear;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }

    public Integer getEdition() { return edition; }
    public void setEdition(Integer edition) { this.edition = edition; }

    public BookCondition getCondition() { return condition; }
    public void setCondition(BookCondition condition) { this.condition = condition; }

    public Boolean getCanBorrow() { return canBorrow; }
    public void setCanBorrow(Boolean canBorrow) { this.canBorrow = canBorrow; }

    public Set<Author> getAuthors() { return authors; }
    public void setAuthors(Set<Author> authors) { this.authors = authors; }

    public Set<Genre> getGenres() { return genres; }
    public void setGenres(Set<Genre> genres) { this.genres = genres; }

    public Bookshelf getBookshelf() { return bookshelf; }
    public void setBookshelf(Bookshelf bookshelf) { this.bookshelf = bookshelf; }

    public List<BorrowRecordItem> getBorrowRecordItems() { return borrowRecordItems; }
    public void setBorrowRecordItems(List<BorrowRecordItem> borrowRecordItems) { this.borrowRecordItems = borrowRecordItems; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public boolean isAvailable() {
        if (!canBorrow) return false;
        if (borrowRecordItems == null) return true;
        return borrowRecordItems.stream()
            .noneMatch(item -> item.getReturnDate() == null);
    }

    public String getAuthorsString() {
        if (authors == null || authors.isEmpty()) return "";
        return authors.stream()
            .map(Author::getFullName)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }

    public String getGenresString() {
        if (genres == null || genres.isEmpty()) return "";
        return genres.stream()
            .map(Genre::getName)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }
}

