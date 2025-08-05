package com.library.controller.user;

import com.library.entity.User;
import com.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Hiển thị trang thông tin cá nhân của người dùng hiện tại
     */
    @GetMapping
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        model.addAttribute("user", user);
        return "user/profile";
    }

    /**
     * Hiển thị form chỉnh sửa thông tin cá nhân
     */
    @GetMapping("/edit")
    public String editProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Cập nhật thông tin cá nhân
     */
    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute("user") @Valid User userInput,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        String username = userDetails.getUsername();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Gán đúng ID (tránh người dùng cố tình sửa form để sửa người khác)
        userInput.setId(currentUser.getId());

        try {
            userService.updateUserProfile(userInput);
            model.addAttribute("success", "Cập nhật thông tin cá nhân thành công.");
        } catch (Exception e) {
            model.addAttribute("error", "Cập nhật thất bại: " + e.getMessage());
        }

        return "profile";
    }
}
