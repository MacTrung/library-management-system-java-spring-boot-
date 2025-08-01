package com.library.controller.admin;

import com.library.entity.Author;
import com.library.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/authors")
public class AdminAuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    public String listAuthors(@RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "birthYear", required = false) Integer birthYear,
                              @RequestParam(value = "deathYear", required = false) Integer deathYear,
                              Pageable pageable,
                              Model model) {
        model.addAttribute("authors", authorService.findAuthors(keyword, birthYear, deathYear, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("birthYear", birthYear);
        model.addAttribute("deathYear", deathYear);
        return "admin/authors/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("author", new Author());
        return "admin/authors/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("author", authorService.findById(id).orElseThrow());
        return "admin/authors/form";
    }

    @PostMapping
    public String saveAuthor(@ModelAttribute Author author) {
        if (author.getId() == null) {
            authorService.createAuthor(author);
        } else {
            authorService.updateAuthor(author);
        }
        return "redirect:/admin/authors";
    }

    @PostMapping("/{id}/delete")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return "redirect:/admin/authors";
    }
}
