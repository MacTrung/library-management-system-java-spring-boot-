package com.library.controller;

import com.library.service.BookService;
import com.library.service.UserService;
import com.library.service.ExtensionRequestService;
import com.library.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ExtensionRequestService extensionRequestService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        CustomUserDetailsService.CustomUserPrincipal principal = 
            (CustomUserDetailsService.CustomUserPrincipal) authentication.getPrincipal();
        
        model.addAttribute("user", principal.getUser());
        
        // Add statistics
        model.addAttribute("totalBooks", bookService.countTotalBooks());
        model.addAttribute("borrowedBooks", bookService.countBorrowedBooks());
        model.addAttribute("overdueBooks", bookService.countOverdueBooks());
        model.addAttribute("activeUsers", userService.countActiveUsers());
        model.addAttribute("pendingRequests", extensionRequestService.countPendingRequests());
        
        if (principal.getUser().getRole().name().equals("ADMIN")) {
            return "admin/dashboard";
        } else {
            return "user/dashboard";
        }
    }
}
