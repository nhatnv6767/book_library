package ra.librarymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    public String index(Model model) {
        model.addAttribute("books", bookService.findAll());
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
            bookService.update(book);
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
            if (!file.isEmpty()) {
                String filename = FileUploadUtil.saveFile(file, "books");
                book.setCoverImage(filename);
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
                         @RequestParam(required = false) String category,
                         @RequestParam(required = false) BookStatus status,
                         Model model) {
        List<Book> books;
        if (keyword != null && !keyword.trim().isEmpty()) {
            books = bookService.findByTitleContaining(keyword);
        } else if (category != null && !category.trim().isEmpty()) {
            books = bookService.findByCategory(category);
        } else if (status != null) {
            books = bookService.findByStatus(status);
        } else {
            books = bookService.findAll();
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("categories", getBookCategories());
        model.addAttribute("statuses", BookStatus.values());
        return "admin/books/index";
    }


}
