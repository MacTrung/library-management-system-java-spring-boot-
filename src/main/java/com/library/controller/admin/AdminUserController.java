package com.library.controller.admin;

import com.library.entity.User;
import com.library.entity.UserStatus;
import com.library.service.BorrowService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private BorrowService borrowService;

    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "") String keyword,
                           @RequestParam(required = false) UserStatus status,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "username") String sortBy,
                           @RequestParam(defaultValue = "asc") String sortDir,
                           Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<User> users = userService.findUsers(keyword, status, pageable);
        
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("statuses", UserStatus.values());
        
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/form";
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "admin/users/form";
    }

    @PostMapping
    public String saveUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            if (user.getId() == null) {
                userService.createUser(user);
                redirectAttributes.addFlashAttribute("success", "Thêm người dùng thành công!");
            } else {
                userService.updateUser(user);
                redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công!");
            }
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/borrow-records")
    public String viewUserBorrowRecords(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        LocalDate fromDate = LocalDate.now().minusDays(30);
        
        model.addAttribute("user", user);
        model.addAttribute("borrowRecords", borrowService.findByBorrowerAndDateRange(user, fromDate));
        
        return "admin/users/borrow-records";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
