package com.library.controller.admin;

import com.library.entity.BorrowRecord;
import com.library.entity.BorrowRecordItem;
import com.library.entity.ReturnStatus;
import com.library.entity.User;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin/borrows")
public class AdminBorrowController {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @GetMapping
    public String listBorrowRecords(@RequestParam(defaultValue = "") String keyword,
                                    @RequestParam(required = false) ReturnStatus returnStatus,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "statusSort") String sortBy,
                                    @RequestParam(defaultValue = "desc") String sortDir,
                                    Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());
        Page<BorrowRecord> records = borrowService.findBorrowRecords(keyword, null, fromDate, toDate, pageable);


        List<BorrowRecord> sortedList = new ArrayList<>(records.getContent());

        if ("statusSort".equals(sortBy)) {
            // Ưu tiên: chưa trả > trả trễ > đúng hạn > bị hỏng
            sortedList.sort(Comparator.comparing((BorrowRecord r) ->
                    r.getItems().stream()
                            .map(i -> i.getReturnStatus())
                            .min(Comparator.comparing(this::getStatusPriority))
                            .orElse(ReturnStatus.NOT_RETURNED)
            ));
        }

        Page<BorrowRecord> finalPage = new PageImpl<>(sortedList, pageable, records.getTotalElements());

        model.addAttribute("records", finalPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("returnStatus", returnStatus);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("statuses", Arrays.asList(ReturnStatus.values()));

        return "admin/borrows/list";
    }

    @GetMapping("/{id}")
    public String viewBorrowDetail(@PathVariable Long id, Model model) {
        BorrowRecord record = borrowService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn mượn"));

        model.addAttribute("record", record);
        return "admin/borrows/detail";
    }

    @PostMapping("/{id}/delete")
    public String deleteBorrow(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            borrowService.deleteBorrowRecord(id);
            redirect.addFlashAttribute("success", "Xóa đơn mượn thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể xóa đơn mượn: " + e.getMessage());
        }
        return "redirect:/admin/borrows";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        BorrowRecord record = new BorrowRecord();
        record.setBorrowDate(LocalDate.now());
        record.setDeposit(new BigDecimal("15000")); // mặc định
        record.getItems().add(new BorrowRecordItem()); // Thêm 1 dòng để chọn sách

        model.addAttribute("record", record);
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("books", bookService.findAllAvailableBooks());

        return "admin/borrows/form";
    }



    @PostMapping("/save")
    public String save(@ModelAttribute BorrowRecord borrowRecord,
                       RedirectAttributes redirect) {
        try {
            if (borrowRecord.getBorrowDate() == null) {
                borrowRecord.setBorrowDate(LocalDate.now());
            }
            if (borrowRecord.getDeposit() == null) {
                borrowRecord.setDeposit(new BigDecimal("15000"));
            }

            // Gán lại thực thể từ ID (nếu form chỉ truyền ID)
            Long userId = borrowRecord.getBorrower() != null ? borrowRecord.getBorrower().getId() : null;
            if (userId != null) {
                userService.findById(userId).ifPresent(borrowRecord::setBorrower);
            }

            for (BorrowRecordItem item : borrowRecord.getItems()) {
                Long bookId = item.getBook() != null ? item.getBook().getId() : null;
                if (bookId != null) {
                    bookService.findById(bookId).ifPresent(item::setBook);
                }
            }

            if (borrowRecord.getId() == null) {

                for (BorrowRecordItem item : borrowRecord.getItems()) {
                    item.setBorrowRecord(borrowRecord); // gán ngược lại để mapping chuẩn
                    if (item.getReturnStatus() == null) {
                        item.setReturnStatus(ReturnStatus.NOT_RETURNED);
                    }
                }

                borrowService.createBorrowRecord(borrowRecord);
                redirect.addFlashAttribute("success", "Tạo đơn mượn thành công!");
            } else {
                borrowService.updateBorrowRecord(borrowRecord);
                redirect.addFlashAttribute("success", "Cập nhật đơn mượn thành công!");
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/borrows";
    }



    private int getStatusPriority(ReturnStatus status) {
        switch (status) {
            case NOT_RETURNED: return 0;
            case RETURNED_LATE: return 1;
            case RETURNED_ON_TIME: return 2;
            case DAMAGED: return 3;
            default: return 99;
        }
    }
}
