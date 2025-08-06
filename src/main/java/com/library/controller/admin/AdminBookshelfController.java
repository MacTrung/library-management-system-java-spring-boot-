package com.library.controller.admin;

import com.library.entity.Bookshelf;
import com.library.service.BookshelfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/bookshelves")
public class AdminBookshelfController {

    @Autowired
    private BookshelfService bookshelfService;

    // GET: List with filters
    @GetMapping
    public String listBookshelves(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Integer floor,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        Page<Bookshelf> pageData = bookshelfService.findBookshelves(keyword, floor, PageRequest.of(page, size));
        model.addAttribute("pageData", pageData);
        model.addAttribute("keyword", keyword);
        model.addAttribute("floor", floor);
        return "admin/bookshelf/list";
    }

    // GET: Create form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("bookshelf", new Bookshelf());
        return "admin/bookshelf/form";
    }

    // POST: Save new
    @PostMapping("/create")
    public String createBookshelf(@Valid @ModelAttribute Bookshelf bookshelf,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "admin/bookshelf/form";
        }

        bookshelfService.createBookshelf(bookshelf);
        return "redirect:/admin/bookshelves";
    }

    // GET: Edit form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Bookshelf bookshelf = bookshelfService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bookshelf ID"));

        model.addAttribute("bookshelf", bookshelf);
        return "admin/bookshelf/form";
    }

    // POST: Update existing
    @PostMapping("/{id}/edit")
    public String updateBookshelf(@PathVariable Long id,
                                  @Valid @ModelAttribute Bookshelf bookshelf,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "admin/bookshelf/form";
        }

        bookshelf.setId(id); // đảm bảo giữ ID cũ
        bookshelfService.updateBookshelf(bookshelf);
        return "redirect:/admin/bookshelves";
    }


    @PostMapping("/{id}/delete")
    public String deleteBookshelf(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookshelfService.deleteBookshelf(id);
            redirectAttributes.addFlashAttribute("success", "Xoá tủ sách thành công");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Không thể xoá tủ sách vì vẫn còn sách trong tủ.");
        }
        return "redirect:/admin/bookshelves";
    }

}
