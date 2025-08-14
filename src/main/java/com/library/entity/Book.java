package com.library.entity;

import jakarta.persistence.*;
import lombok.*;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

