package ra.librarymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.librarymanagement.model.BorrowRecord.BorrowRecord;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;
import ra.librarymanagement.service.IBookService;
import ra.librarymanagement.service.IBorrowRecordService;
import ra.librarymanagement.service.IMemberService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/borrows")
public class BorrowRecordController {
    private final IBorrowRecordService borrowRecordService;
    private final IBookService bookService;
    private final IMemberService memberService;

    @Autowired
    public BorrowRecordController(IBorrowRecordService borrowRecordService, IBookService bookService, IMemberService memberService) {
        this.borrowRecordService = borrowRecordService;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("borrows", borrowRecordService.findAll());
        return "admin/borrows/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("borrow", new BorrowRecord());
        model.addAttribute("books", bookService.findAvailableBooks());
        model.addAttribute("members", memberService.findAll());
        return "admin/borrows/form";
    }

    @PostMapping("/add")
    public String add(@RequestParam Long memberId,
                      @RequestParam Long bookId,
                      RedirectAttributes redirectAttributes) {
        try {
            borrowRecordService.borrowBook(memberId, bookId);
            redirectAttributes.addFlashAttribute("successMessage", "Book borrowed successfully");
            return "redirect:/admin/borrows";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Could not borrow book: " + e.getMessage());
            return "redirect:/admin/borrows/add";
        }
    }

    @GetMapping("/return/{id}")
    public String returnBook(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            borrowRecordService.returnBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Could not return book: " + e.getMessage());
        }
        return "redirect:/admin/borrows";
    }

    @GetMapping("/extend/{id}")
    public String extendBorrow(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            // Extend borrow period
            // If successful, add success message
            if (borrowRecordService.extendBorrowPeriod(id)) {
                redirectAttributes.addFlashAttribute("successMessage", "Borrow period extended successfully");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Could not extend borrow period");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Could not extend borrow period: " + e.getMessage());
        }
        return "redirect:/admin/borrows";
    }

    @GetMapping("/lost/{id}")
    public String reportLost(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            borrowRecordService.reportLostBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book reported as lost successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Could not report book as lost: " + e.getMessage());
        }
        return "redirect:/admin/borrows";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) BorrowStatus status,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                         Model model) {
        List<BorrowRecord> borrows;
        if (startDate != null && endDate != null) {
            borrows = borrowRecordService.findByDateRange(startDate, endDate);
        } else if (status != null) {
            borrows = borrowRecordService.findByStatus(status);
        } else {
            borrows = borrowRecordService.findAll();
        }
        model.addAttribute("borrows", borrows);
        model.addAttribute("statuses", BorrowStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/borrows/index";
    }
}
