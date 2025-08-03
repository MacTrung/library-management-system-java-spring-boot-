
package com.library.controller.user;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.BorrowRecordItem;
import com.library.entity.User;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user/borrow")
public class UserBorrowController {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @GetMapping
    public String viewBorrowHistory(@RequestParam(defaultValue = "") String keyword,
                                    @RequestParam(required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                    @RequestParam(required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Principal principal,
                                    Model model) {

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Pageable pageable = PageRequest.of(page, size);
        Page<BorrowRecord> borrowRecords = borrowService.findBorrowRecords(keyword, user, fromDate, toDate, pageable);

        model.addAttribute("records", borrowRecords);
        model.addAttribute("keyword", keyword);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        return "user/borrow/list";
    }

    @GetMapping("/{id}")
    public String viewBorrowRecordDetail(@PathVariable Long id,
                                         Principal principal,
                                         Model model) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        BorrowRecord record = borrowService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn mượn"));

        if (!record.getBorrower().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền truy cập đơn mượn này");
        }

        model.addAttribute("record", record);
        return "user/borrow/detail";
    }

    @ModelAttribute("hasOverdueBooks")
    public boolean hasOverdueBooks(Principal principal) {
        if (principal == null) return false;

        return userService.findByUsername(principal.getName())
                .map(borrowService::hasOverdueBooks)
                .orElse(false);
    }

    @ModelAttribute("hasDueSoonBooks")
    public boolean hasDueSoonBooks(Principal principal) {
        if (principal == null) return false;

        return userService.findByUsername(principal.getName())
                .map(user -> borrowService.hasDueSoonBooks(user, 3)) // cảnh báo trước 3 ngày
                .orElse(false);
    }

    @ModelAttribute("canBorrow")
    public boolean canBorrow(Principal principal) {
        if (principal == null) return false;

        return userService.findByUsername(principal.getName())
                .map(borrowService::canUserBorrow)
                .orElse(false);
    }

    @GetMapping("/request/{bookId}")
    public String showBorrowRequestForm(@PathVariable("bookId") Long bookId,
                                        Principal principal,
                                        Model model,
                                        RedirectAttributes redirect) {

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));

        // Kiểm tra điều kiện
        if (borrowService.isBookCurrentlyBorrowedByUser(user, book)) {
            redirect.addFlashAttribute("error", "Bạn đã mượn sách này và chưa trả. Không thể mượn lại.");
            return "redirect:/books";
        }

        if (!book.getCanBorrow()) {
            throw new RuntimeException("Sách này hiện không thể mượn.");
        }

        if (!user.isActive() || borrowService.hasOverdueBooks(user)) {
            throw new RuntimeException("Bạn đang bị giới hạn quyền mượn sách.");
        }

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBorrower(user);
        borrowRecord.setBorrowDate(LocalDate.now());

        // Gán book vào BorrowRecordItem
        BorrowRecordItem item = new BorrowRecordItem();
        item.setBook(book);
        item.setExpectedReturnDate(LocalDate.now().plusDays(14)); // ví dụ: 2 tuần

        borrowRecord.getItems().add(item);

        model.addAttribute("record", borrowRecord);
        model.addAttribute("book", book);

        return "user/borrow/request";
    }

    @PostMapping("/submit")
    public String submitBorrowRequest(@ModelAttribute("record") BorrowRecord borrowRecord,
                                      Principal principal,
                                      RedirectAttributes redirect) {

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        borrowRecord.setBorrower(user);
        borrowRecord.setDeposit(BigDecimal.valueOf(15000));
        borrowRecord.setBorrowDate(LocalDate.now());

        for (BorrowRecordItem item : borrowRecord.getItems()) {
            item.setExpectedReturnDate(LocalDate.now().plusDays(15));
            item.setBorrowRecord(borrowRecord);
        }

        borrowService.createBorrowRecord(borrowRecord);
        redirect.addFlashAttribute("success", "Đã gửi yêu cầu mượn thành công!");

        return "redirect:/user/borrow";
    }

}
