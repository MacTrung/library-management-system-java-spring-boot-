package com.library.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Import các exception domain
import com.library.exception.custom.UserNotFoundException;
import com.library.exception.custom.BookNotAvailableException;
import com.library.exception.custom.BorrowRecordNotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý lỗi khi không tìm thấy User
     */
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException e, Model model) {
        log.warn("User not found: {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        return "error/error";
    }

    /**
     * Xử lý lỗi khi sách không khả dụng để mượn
     */
    @ExceptionHandler(BookNotAvailableException.class)
    public String handleBookNotAvailable(BookNotAvailableException e, Model model) {
        log.warn("Book not available: {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        return "error/error";
    }

    /**
     * Xử lý lỗi khi không tìm thấy đơn mượn sách
     */
    @ExceptionHandler(BorrowRecordNotFoundException.class)
    public String handleBorrowRecordNotFound(BorrowRecordNotFoundException e, Model model) {
        log.warn("Borrow record not found: {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        return "error/error";
    }

    /**
     * Xử lý tất cả lỗi không mong đợi khác
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception e, Model model) {
        log.error("Unexpected error occurred", e);
        model.addAttribute("error", "Lỗi hệ thống, vui lòng thử lại sau!");
        return "error/error";
    }
}
