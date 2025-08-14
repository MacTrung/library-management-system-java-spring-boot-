package com.library.controller.admin;

import com.library.entity.ExtensionRequest;
import com.library.entity.ExtensionStatus;
import com.library.service.BookService;
import com.library.service.ExtensionRequestService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/extension-requests")
public class AdminExtensionRqController {

    @Autowired
    private ExtensionRequestService extensionRequestService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    // GET: List with filters
    @GetMapping
    public String listRequests(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ExtensionStatus status,
            @RequestParam(required = false) Long borrowerId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        var borrower = (borrowerId != null) ? userService.findById(borrowerId).orElse(null) : null;

        Page<ExtensionRequest> pageData = extensionRequestService.findExtensionRequests(
                keyword, status, borrower, fromDate, toDate, pageable
        );

        model.addAttribute("pageData", pageData);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("borrowerId", borrowerId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("statusList", ExtensionStatus.values());

        return "admin/extension/list";
    }

    // GET: Detail
    @GetMapping("/{id}")
    public String viewRequest(@PathVariable Long id, Model model) {
        ExtensionRequest request = extensionRequestService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID"));

        model.addAttribute("request", request);
        return "admin/extension/detail";
    }

    // GET: Edit form
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ExtensionRequest request = extensionRequestService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID"));
        model.addAttribute("request", request);
        model.addAttribute("statusList", ExtensionStatus.values());
        return "admin/extension/edit";
    }

    // POST: Update
    @PostMapping("/{id}/edit")
    public String updateRequest(@PathVariable Long id,
                                @ModelAttribute ExtensionRequest updatedRequest) {
        ExtensionRequest existing = extensionRequestService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID"));

        existing.setStatus(updatedRequest.getStatus());
        existing.setReason(updatedRequest.getReason());
        extensionRequestService.updateExtensionRequest(existing);

        return "redirect:/admin/extension-requests";
    }

    // POST: Cancel one
    @PostMapping("/{id}/cancel")
    public String cancelRequest(@PathVariable Long id) {
        extensionRequestService.cancelExtensionRequest(id);
        return "redirect:/admin/extension-requests";
    }

    // POST: khi chọn nhiều thực hiện liên tục
    @PostMapping("/bulk-process")
    public String bulkUpdateStatus(@RequestParam List<Long> selectedIds,
                                   @RequestParam ExtensionStatus status) {
        extensionRequestService.processMultipleRequests(selectedIds, status);
        return "redirect:/admin/extension-requests";
    }
}
