package ra.librarymanagement.controller;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import paging.PageResponse;
import ra.librarymanagement.constants.LibraryConstants;
import ra.librarymanagement.model.BorrowRecord.BorrowRecord;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;
import ra.librarymanagement.model.book.Book;
import ra.librarymanagement.model.member.Member;
import ra.librarymanagement.repository.imp.BorrowRecordRepositoryImp;
import ra.librarymanagement.service.IBookService;
import ra.librarymanagement.service.IBorrowRecordService;
import ra.librarymanagement.service.IMemberService;
import ra.librarymanagement.service.imp.BorrowRecordServiceImp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/borrows")
public class BorrowRecordController {
    private final IBorrowRecordService borrowRecordService;
    private final IBookService bookService;
    private final IMemberService memberService;
    
    private static final Logger logger = LoggerFactory.getLogger(BorrowRecordRepositoryImp.class);

    @Autowired
    public BorrowRecordController(IBorrowRecordService borrowRecordService, IBookService bookService, IMemberService memberService) {
        this.borrowRecordService = borrowRecordService;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        PageResponse<BorrowRecord> borrows = borrowRecordService.searchBorrowRecords(
                null, null, null, null, page, size);

        borrows.getContent().forEach(borrow -> {
            Hibernate.initialize(borrow.getMember());
            Hibernate.initialize(borrow.getBook());
        });

        model.addAttribute("borrows", borrows);
        model.addAttribute("statuses", BorrowStatus.values());
        return "admin/borrows/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {

        List<Member> activeMembers = memberService.findActiveMembers();
        List<Book> availableBooks = bookService.findAvailableBooks();

        // availableBooks.forEach(book -> {
        //     System.out.println("Book: " + book.getTitle());
        //     System.out.println("ISBN: " + book.getIsbn());
        //     System.out.println("Author: " + book.getAuthor());
        //     System.out.println("Category: " + book.getCategory());
        //     System.out.println("Publisher: " + book.getPublisher());
        //     System.out.println("Quantity: " + book.getQuantity());
        //     System.out.println("-------------------");
        // });

        Map<Long, Long> memberActiveBorrows = borrowRecordService.getActiveBorrowsCountByMembers(activeMembers.stream().map(Member::getMemberId).collect(Collectors.toList()));


        model.addAttribute("borrow", new BorrowRecord());
        model.addAttribute("books", availableBooks);
        model.addAttribute("members", activeMembers);
        model.addAttribute("memberActiveBorrows", memberActiveBorrows);

        model.addAttribute("maxBorrowDays", LibraryConstants.DEFAULT_BORROW_DAYS);
        model.addAttribute("lateFeePerDay", LibraryConstants.DAILY_FINE);
        // get max books for each member type
        model.addAttribute("regularMaxBooks", LibraryConstants.REGULAR_MEMBER_MAX_BOOKS);
        model.addAttribute("vipMaxBooks", LibraryConstants.VIP_MEMBER_MAX_BOOKS);
        model.addAttribute("studentMaxBooks", LibraryConstants.STUDENT_MEMBER_MAX_BOOKS);
        // model.addAttribute("memberActiveBorrows", memberActiveBorrows);
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
    public String handleExtendBorrowGet(@PathVariable Long id,
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
    @Transactional(readOnly = true)
    public String search(
            @RequestParam(required = false) String memberSearch,
            @RequestParam(required = false) BorrowStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        PageResponse<BorrowRecord> borrows = borrowRecordService.searchBorrowRecords(
                memberSearch, status, startDate, endDate, page, size);

        borrows.getContent().forEach(borrow -> {
            Hibernate.initialize(borrow.getMember());
            Hibernate.initialize(borrow.getBook());
        });

        model.addAttribute("borrows", borrows);
        model.addAttribute("statuses", BorrowStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("memberSearch", memberSearch);
        return "admin/borrows/index";
    }

    @GetMapping("/view/{id}")
    @Transactional(readOnly = true)
    public String viewBorrow(@PathVariable Long id, Model model) {
        try {
            BorrowRecord borrow = borrowRecordService.findByIdWithMember(id).orElseThrow(() -> new IllegalArgumentException("Invalid borrow ID"));

            Hibernate.initialize(borrow.getMember());
            Hibernate.initialize(borrow.getBook());

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            model.addAttribute("dateFormatter", dateFormatter);
            model.addAttribute("timeFormatter", timeFormatter);

            LocalDateTime now = LocalDateTime.now();
            model.addAttribute("borrow", borrow);
            model.addAttribute("now", now);

            // calculate days until due/overdue
            if (borrow.getStatus() == BorrowStatus.BORROWING) {
                long daysUntilDue = ChronoUnit.DAYS.between(now, borrow.getDueDate());
                if (daysUntilDue > 0) {
                    model.addAttribute("daysUntilDue", daysUntilDue);
                } else {
                    model.addAttribute("daysOverdue", Math.abs(daysUntilDue));
                }
            }

            // Calculate new due date for extension
            if (borrow.getStatus() == BorrowStatus.BORROWING && borrow.getExtensionCount() < 2) {
                LocalDateTime newDueDate = borrow.getDueDate().plusDays(LibraryConstants.DEFAULT_BORROW_DAYS);
                model.addAttribute("newDueDate", newDueDate);
            }

            // add constants for fines
            model.addAttribute("lostBookFine", LibraryConstants.LOST_BOOK_FINE);

            return "admin/borrows/view";
        } catch (Exception e) {
            logger.error("Error viewing borrow record: ", e);
            throw e; // Re-throw để xem stack trace đầy đủ
        }
    }

    @PostMapping("/return/{id}")
    public String returnBook(@PathVariable Long id,
                             @RequestParam String condition,
                             RedirectAttributes redirectAttributes) {
        try {
            Optional<BorrowRecord> optionalBorrow = borrowRecordService.findById(id);
            if (optionalBorrow.isPresent()) {
                BorrowRecord borrow = optionalBorrow.get();
                borrow.setActualReturnCondition(condition);
                BorrowRecord returned = borrowRecordService.returnBook(id);
                redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Borrow record not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not return book: " + e.getMessage());
        }
        return "redirect:/admin/borrows/view/" + id;
    }

    @PostMapping("/extend/{id}")
    public String handleExtendBorrowPost(@PathVariable Long id,
                                         RedirectAttributes redirectAttributes) {
        try {
            if (borrowRecordService.findById(id).isPresent()) {
                if (borrowRecordService.extendBorrowPeriod(id)) {
                    redirectAttributes.addFlashAttribute("successMessage", "Borrow period extended successfully");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Could not extend borrow period");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Borrow record not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error extending borrow period: " + e.getMessage());
        }
        return "redirect:/admin/borrows/view/" + id;
    }

    @PostMapping("/lost/{id}")
    public String reportLost(@PathVariable Long id,
                             @RequestParam String notes,
                             RedirectAttributes redirectAttributes) {
        try {
            BorrowRecord record = borrowRecordService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Borrow record not found with id: " + id));

            record.setNote(notes);
            borrowRecordService.reportLostBook(id);

            redirectAttributes.addFlashAttribute("successMessage", "Book reported as lost successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error reporting book as lost: " + e.getMessage());
        }
        return "redirect:/admin/borrows/view/" + id;
    }
}
