package ra.librarymanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ra.librarymanagement.service.IBookService;
import ra.librarymanagement.service.IBorrowRecordService;
import ra.librarymanagement.service.IMemberService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class DashboardController {
    private final IBookService bookService;
    private final IMemberService memberService;
    private final IBorrowRecordService borrowRecordService;

    @Autowired
    public DashboardController(IBookService bookService, IMemberService memberService, IBorrowRecordService borrowRecordService) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.borrowRecordService = borrowRecordService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) throws JsonProcessingException {
        model.addAttribute("totalBooks", bookService.countTotalBooks());
        model.addAttribute("availableBooks", bookService.countAvailableBooks());
        model.addAttribute("activeMembers", memberService.countActiveMembers());
        model.addAttribute("newMembersThisMonth", memberService.countNewMembersThisMonth());
        model.addAttribute("currentBorrows", borrowRecordService.countCurrentBorrows());
        model.addAttribute("overdueBorrows", borrowRecordService.countOverdueBorrows());
        model.addAttribute("totalFine", borrowRecordService.calculateTotalFinesThisMonth());

        // Borrows trends data
        List<String> borrowRendsLabels = new ArrayList<>();
        List<Integer> borrowTrendsData = new ArrayList<>();
        Map<String, Integer> trends = borrowRecordService.getBorrowTrendsLastSixMonths();
        borrowRendsLabels.addAll(trends.keySet());
        borrowTrendsData.addAll(trends.values());
        model.addAttribute("borrowTrendsLabels", new ObjectMapper().writeValueAsString(borrowRendsLabels));
        model.addAttribute("borrowTrendsData", new ObjectMapper().writeValueAsString(borrowTrendsData));

        // popular books data
        Map<String, Integer> popularBooks = borrowRecordService.getMostPopularBooks(5);
        model.addAttribute("popularBooksLabels", new ObjectMapper().writeValueAsString(popularBooks.keySet()));
        model.addAttribute("popularBooksData", new ObjectMapper().writeValueAsString(popularBooks.values()));

        // recent activities
        model.addAttribute("recentActivities", borrowRecordService.getRecentActivities(10));

        // alerts & Notifications
        model.addAttribute("alerts", borrowRecordService.getActiveAlerts());

        return "admin/dashboard";

    }
}
