package com.library.controller.user;

import com.library.entity.ExtensionRequest;
import com.library.entity.ExtensionStatus;
import com.library.entity.User;
import com.library.service.BorrowService;
import com.library.service.ExtensionRequestService;
import com.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/user/extensions")
public class UserExtensionRqController {

    @Autowired
    private ExtensionRequestService extensionRequestService;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // GET - Hiển thị danh sách yêu cầu gia hạn của người dùng
    @GetMapping
    public String listRequests(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) ExtensionStatus status,
                               @RequestParam(required = false) String fromDate,
                               @RequestParam(required = false) String toDate,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {

        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);

        LocalDateTime from = null;
        LocalDateTime to = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (fromDate != null && !fromDate.isBlank()) {
            from = LocalDate.parse(fromDate, formatter).atStartOfDay();
        }
        if (toDate != null && !toDate.isBlank()) {
            to = LocalDate.parse(toDate, formatter).atTime(23, 59, 59);
        }

        Page<ExtensionRequest> pageResult = extensionRequestService.findExtensionRequests(
                keyword, status, user, from, to, pageable);

        model.addAttribute("requests", pageResult);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "user/extensions/list";
    }

    // GET - Hiển thị form tạo yêu cầu
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        User user = getCurrentUser();

        if (!user.isActive()) {
            model.addAttribute("error", "Tài khoản không hoạt động. Không thể tạo yêu cầu.");
            return "user/extensions/list";
        }

        model.addAttribute("extensionRequest", new ExtensionRequest());
        model.addAttribute("borrowRecords", borrowService.findByBorrower(user));
        return "user/extensions/create";
    }

    // POST - Gửi yêu cầu gia hạn
    @PostMapping("/create")
    public String createRequest(@ModelAttribute("extensionRequest") @Valid ExtensionRequest extensionRequest,
                                @RequestParam("borrowRecordId") Long borrowRecordId,
                                BindingResult result,
                                Model model) {
        User user = getCurrentUser();

        if (!user.isActive()) {
            model.addAttribute("error", "Tài khoản không hoạt động. Không thể tạo yêu cầu.");
            return "user/extensions/create";
        }

        if (result.hasErrors()) {
            model.addAttribute("borrowRecords", borrowService.findByBorrower(user));
            return "user/extensions/create";
        }

        // Gán thủ công borrowRecord cho extensionRequest
        extensionRequest.setBorrowRecord(
                borrowService.findById(borrowRecordId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn mượn"))
        );

        extensionRequest.setStatus(ExtensionStatus.CREATED);
        extensionRequestService.createExtensionRequest(extensionRequest);

        return "redirect:/user/extensions";
    }


    // POST - Hủy yêu cầu gia hạn
    @PostMapping("/{id}/cancel")
    public String cancelRequest(@PathVariable("id") Long id) {
        ExtensionRequest request = extensionRequestService.findById(id)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại"));

        User user = getCurrentUser();
        if (!request.getBorrowRecord().getBorrower().getId().equals(user.getId())) {
            throw new RuntimeException("Không được phép hủy yêu cầu của người khác");
        }

        extensionRequestService.cancelExtensionRequest(id);
        return "redirect:/user/extensions";
    }
}