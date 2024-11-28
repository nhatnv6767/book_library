package ra.librarymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ra.librarymanagement.paging.PageResponse;
import ra.librarymanagement.model.book.Book;
import ra.librarymanagement.model.book.BookStatus;
import ra.librarymanagement.service.IBookService;
import ra.librarymanagement.util.FileUploadUtil;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/books")
public class BookController {
    private final IBookService bookService;

    @Autowired
    public BookController(IBookService bookService) {
        this.bookService = bookService;
    }

    private List<String> getBookCategories() {
        return Arrays.asList(
                "Programming",
                "Literature",
                "Science",
                "History",
                "Mathematics",
                "Biography",
                "Self-help",
                "Bussiness"
        );
    }

    @GetMapping
    @Transactional
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        PageResponse<Book> books = bookService.searchBooks(null, null, null, page, size);

        model.addAttribute("books", books);
        model.addAttribute("categories", getBookCategories());
        model.addAttribute("statuses", BookStatus.values());
        return "admin/books/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", getBookCategories());
        model.addAttribute("statuses", BookStatus.values());
        return "admin/books/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Book book,
                      @RequestParam(value = "coverImageFile") MultipartFile file,
                      RedirectAttributes redirectAttributes) {
        try {
            if (!file.isEmpty()) {
                String filename = FileUploadUtil.saveFile(file, "books");
                book.setCoverImage(filename);
            }
            bookService.save(book);
            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully");
            return "redirect:/admin/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book add failed: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/books/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        model.addAttribute("book", book);
        model.addAttribute("categories", getBookCategories());
        model.addAttribute("statuses", BookStatus.values());
        return "admin/books/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Book book,
                         @RequestParam(value = "coverImageFile", required = false) MultipartFile file,
                         RedirectAttributes redirectAttributes) {
        try {

            Book existingBook = bookService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

            if (!file.isEmpty() && file != null) {

                if (existingBook.getCoverImage() != null) {
                    FileUploadUtil.deleteFile(existingBook.getCoverImage());
                }

                String filename = FileUploadUtil.saveFile(file, "books");
                book.setCoverImage(filename);
            } else {
                book.setCoverImage(existingBook.getCoverImage());
            }
            bookService.update(book);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully");
            return "redirect:/admin/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book update failed: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/books/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book delete failed. It may have active borrowers: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/books";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) BookStatus status,
                         @RequestParam(required = false) String category,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        PageResponse<Book> books = bookService.searchBooks(
                keyword != null ? keyword.trim() : null,
                status,
                category != null ? category.trim() : null,
                page,
                size
        );

        model.addAttribute("books", books);
        model.addAttribute("currentPage", books.getPageNumber());
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("categories", getBookCategories());
        model.addAttribute("statuses", BookStatus.values());
        return "admin/books/index";
    }

    @GetMapping("/view/{id}")
    @Transactional
    public String viewDetails(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        // Force initialize borrowRecords
        book.getBorrowRecords().forEach(record -> {
            record.getMember().getFullName();
        });
        model.addAttribute("book", book);
        return "admin/books/view";
    }
}
