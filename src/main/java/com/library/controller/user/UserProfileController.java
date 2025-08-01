package com.library.controller.user;

import com.library.entity.User;
import com.library.service.CustomUserDetailsService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/profile")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewProfile(Authentication authentication, Model model) {
        CustomUserDetailsService.CustomUserPrincipal principal = 
            (CustomUserDetailsService.CustomUserPrincipal) authentication.getPrincipal();
        
        model.addAttribute("user", principal.getUser());
        return "user/profile";
    }

    @GetMapping("/edit")
    public String editProfile(Authentication authentication, Model model) {
        CustomUserDetailsService.CustomUserPrincipal principal = 
            (CustomUserDetailsService.CustomUserPrincipal) authentication.getPrincipal();
        
        model.addAttribute("user", principal.getUser());
        return "user/profile-edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute User user, 
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            CustomUserDetailsService.CustomUserPrincipal principal = 
                (CustomUserDetailsService.CustomUserPrincipal) authentication.getPrincipal();
            
            user.setId(principal.getUser().getId());
            userService.updateUserProfile(user);
            
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
            return "redirect:/user/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/user/profile/edit";
        }
    }
}
