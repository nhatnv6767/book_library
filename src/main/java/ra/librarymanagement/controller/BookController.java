package ra.librarymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @GetMapping
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
                      @RequestParam(value = "coverImageFile", required = false) MultipartFile file,
                      RedirectAttributes redirectAttributes) {
        try {
            if (!file.isEmpty()) {
                String filename = FileUploadUtil.saveFile(file, "books");
                book.setCoverImage(filename);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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


}
