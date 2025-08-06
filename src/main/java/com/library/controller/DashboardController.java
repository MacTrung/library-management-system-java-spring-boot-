package com.library.controller;

import com.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ExtensionRequestService extensionRequestService;

    @Autowired
    private BorrowService borrowService;


    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        CustomUserDetailsService.CustomUserPrincipal principal =
                (CustomUserDetailsService.CustomUserPrincipal) authentication.getPrincipal();

        var user = principal.getUser();
        model.addAttribute("user", user);

        if (user.getRole().name().equals("ADMIN")) {
            // Admin statistics
            model.addAttribute("totalBooks", bookService.countTotalBooks());
            model.addAttribute("borrowedBooks", bookService.countBorrowedBooks());
            model.addAttribute("overdueBooks", bookService.countOverdueBooks());
            model.addAttribute("activeUsers", userService.countActiveUsers());
            model.addAttribute("pendingRequests", extensionRequestService.countPendingRequests());
            return "admin/dashboard";
        } else {
            // User statistics
            int borrowedBooks = (int) borrowService.findByBorrower(user).stream()
                    .flatMap(r -> r.getItems().stream())
                    .filter(item -> item.getReturnDate() == null)
                    .count();

            int overdueBooks = (int) borrowService.findByBorrower(user).stream()
                    .flatMap(r -> r.getItems().stream())
                    .filter(item ->
                            item.getReturnDate() == null &&
                                    item.getExpectedReturnDate() != null &&
                                    item.getExpectedReturnDate().isBefore(java.time.LocalDate.now())
                    )
                    .count();

            int extensionCount = extensionRequestService.countByUser(user);

            model.addAttribute("borrowedBooks", borrowedBooks);
            model.addAttribute("overdueBooks", overdueBooks);
            model.addAttribute("extensionCount", extensionCount);

            return "user/dashboard";
        }
    }

    @GetMapping("/admin/stats/borrow-monthly")
    @ResponseBody
    public Map<String, Object> getMonthlyBorrowStats() {
        return borrowService.getMonthlyBorrowStats();
    }

    @GetMapping("/admin/stats/genres")
    @ResponseBody
    public Map<String, Object> getGenreDistribution() {
        return bookService.getGenreDistribution();
    }



}
