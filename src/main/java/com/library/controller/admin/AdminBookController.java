package com.library.controller.admin;

import com.library.entity.Book;
import com.library.entity.BookCondition;
import com.library.service.AuthorService;
import com.library.service.BookService;
import com.library.service.BookshelfService;
import com.library.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/books")
public class AdminBookController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private AuthorService authorService;
    
    @Autowired
    private GenreService genreService;
    
    @Autowired
    private BookshelfService bookshelfService;

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
        
        return "admin/books/list";
    }

    @GetMapping("/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAllAuthors());
        model.addAttribute("genres", genreService.findAllGenres());
        model.addAttribute("bookshelves", bookshelfService.findAllBookshelves());
        model.addAttribute("conditions", BookCondition.values());
        return "admin/books/form";
    }

    @GetMapping("/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAllAuthors());
        model.addAttribute("genres", genreService.findAllGenres());
        model.addAttribute("bookshelves", bookshelfService.findAllBookshelves());
        model.addAttribute("conditions", BookCondition.values());
        return "admin/books/form";
    }

    @PostMapping
    public String saveBook(@ModelAttribute Book book, 
                          @RequestParam(required = false) Long[] authorIds,
                          @RequestParam(required = false) Long[] genreIds,
                          RedirectAttributes redirectAttributes) {
        try {
            // Set authors and genres
            if (authorIds != null) {
                for (Long authorId : authorIds) {
                    authorService.findById(authorId).ifPresent(book.getAuthors()::add);
                }
            }
            
            if (genreIds != null) {
                for (Long genreId : genreIds) {
                    genreService.findById(genreId).ifPresent(book.getGenres()::add);
                }
            }
            
            if (book.getId() == null) {
                bookService.createBook(book);
                redirectAttributes.addFlashAttribute("success", "Thêm sách thành công!");
            } else {
                bookService.updateBook(book);
                redirectAttributes.addFlashAttribute("success", "Cập nhật sách thành công!");
            }
            
            return "redirect:/admin/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/books";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa sách: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    @GetMapping("/books/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));
        model.addAttribute("book", book);
        return "/books/view";
    }


}
