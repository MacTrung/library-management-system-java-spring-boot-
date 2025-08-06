package com.library.controller;

import com.library.entity.Book;
import com.library.entity.BookCondition;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public String listBooks(@RequestParam(defaultValue = "") String keyword,
                           @RequestParam(required = false) BookCondition condition,
                           @RequestParam(required = false) Boolean canBorrow,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "title") String sortBy,
                           @RequestParam(defaultValue = "asc") String sortDir,
                           Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> books = bookService.findBooks(keyword, condition, canBorrow, pageable);
        
        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("condition", condition);
        model.addAttribute("canBorrow", canBorrow);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("conditions", BookCondition.values());
        
        return "/user/books/list";
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        model.addAttribute("book", book);
        return "/user/books/view";
    }
}
