package ra.librarymanagement.service.imp;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import paging.PageResponse;
import ra.librarymanagement.constants.LibraryConstants;
import ra.librarymanagement.model.BorrowRecord.BorrowRecord;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;
import ra.librarymanagement.model.book.Book;
import ra.librarymanagement.model.book.BookStatus;
import ra.librarymanagement.model.member.Member;
import ra.librarymanagement.model.statistic.Alert;
import ra.librarymanagement.model.statistic.RecentActivity;
import ra.librarymanagement.repository.IBorrowRecordRepository;
import ra.librarymanagement.service.IBookService;
import ra.librarymanagement.service.IBorrowRecordService;
import ra.librarymanagement.service.IMemberService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class BorrowRecordServiceImp implements IBorrowRecordService {

    private final IBorrowRecordRepository borrowRecordRepository;
    private final IBookService bookService;
    private final IMemberService memberService;

    @Autowired
    public BorrowRecordServiceImp(IBorrowRecordRepository borrowRecordRepository, IBookService bookService, IMemberService memberService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @Override
    public List<BorrowRecord> findAll() {
        return borrowRecordRepository.findAll();
    }

    @Override
    public Optional<BorrowRecord> findById(Long id) {
        return borrowRecordRepository.findById(id);
    }

    @Override
    public List<BorrowRecord> findByMemberId(Long memberId) {
        return borrowRecordRepository.findByMemberId(memberId);
    }

    @Override
    public List<BorrowRecord> findByBookId(Long bookId) {
        return borrowRecordRepository.findByBookId(bookId);
    }

    @Override
    public List<BorrowRecord> findByStatus(BorrowStatus status) {
        return borrowRecordRepository.findByStatus(status);
    }

    @Override
    public List<BorrowRecord> findOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords();
    }

    @Override
    @Transactional
    public BorrowRecord borrowBook(Long memberId, Long bookId) {

        // check if member can borrow books
        if (!memberService.canBorrowBooks(memberId)) {
            throw new IllegalArgumentException("Member cannot borrow books");
        }

        // check if book is available
        if (!bookService.isAvailableForBorrow(bookId)) {
            throw new IllegalArgumentException("Book is not available for borrow");
        }

        // get member information
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        // create borrow record
        // set borrow date, due date, status, extension count
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setMember(member);
        borrowRecord.setBook(book);
        borrowRecord.setBorrowDate(LocalDateTime.now());
        borrowRecord.setDueDate(LocalDateTime.now().plusDays(LibraryConstants.DEFAULT_BORROW_DAYS));
        borrowRecord.setStatus(BorrowStatus.BORROWING);
        borrowRecord.setExtensionCount(0);

        // update book quantity
        bookService.updateQuantity(bookId, book.getQuantity() - 1);
        // save borrow record
        borrowRecordRepository.save(borrowRecord);
        return borrowRecord;
    }

    @Override
    @Transactional
    public BorrowRecord returnBook(Long borrowId) {

        BorrowRecord borrowRecord = findById(borrowId)
                .orElseThrow(() -> new IllegalArgumentException("Borrow record not found"));
        // check if book is currently borrowed
        if (borrowRecord.getStatus() != BorrowStatus.BORROWING) {
            throw new IllegalArgumentException("Book it not currently borrowed");
        }

        borrowRecord.setReturnDate(LocalDateTime.now());
        borrowRecord.setStatus(BorrowStatus.RETURNED);

        // calculate fine
        calculateFine(borrowId);

        // update book quantity
        Book book = borrowRecord.getBook();
        bookService.updateQuantity(book.getBookId(), book.getQuantity() + 1);

        borrowRecordRepository.update(borrowRecord);

        return borrowRecord;
    }

    @Override
    @Transactional
    public void calculateFine(Long borrowId) {
        BorrowRecord borrowRecord = findById(borrowId)
                .orElseThrow(() -> new IllegalArgumentException("Borrow record not found"));

        LocalDateTime returnDate = borrowRecord.getReturnDate() != null
                ? borrowRecord.getReturnDate()
                : LocalDateTime.now();

        if (returnDate.isAfter(borrowRecord.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(borrowRecord.getDueDate(), returnDate);
            BigDecimal fine = LibraryConstants.DAILY_FINE.multiply(BigDecimal.valueOf(daysLate));
            borrowRecord.setFine(fine);
            borrowRecord.setStatus(BorrowStatus.OVERDUE);
            borrowRecordRepository.update(borrowRecord);
        }
    }

    @Override
    public List<BorrowRecord> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return borrowRecordRepository.findByBorrowDateBetween(startDate, endDate);
    }

    @Override
    public List<BorrowRecord> findActiveBorrowsByMember(Long memberId) {
        return findByBookId(memberId).stream()
                .filter(borrowRecord
                        -> borrowRecord.getStatus() == BorrowStatus.BORROWING)
                .toList();
    }

    @Override
    @Transactional
    public boolean extendBorrowPeriod(Long borrowId) {
        BorrowRecord borrowRecord = findById(borrowId)
                .orElseThrow(() -> new IllegalArgumentException("Borrow record not found"));

        // check if book is currently borrowed
        if (borrowRecord.getStatus() != BorrowStatus.BORROWING) {
            return false;
        }
        // check if book has reached maximum extensions
        if (borrowRecord.getExtensionCount() >= LibraryConstants.MAX_EXTENSIONS) {
            return false;
        }
        // check if book is overdue
        if (LocalDateTime.now().isAfter(borrowRecord.getDueDate())) {
            return false;
        }

        // extend borrow period
        // increase due date by DEFAULT_BORROW_DAYS
        borrowRecord.setDueDate(borrowRecord.getDueDate().plusDays(LibraryConstants.DEFAULT_BORROW_DAYS));
        // increase extension count
        borrowRecord.setExtensionCount(borrowRecord.getExtensionCount() + 1);

        borrowRecordRepository.update(borrowRecord);

        return true;
    }

    @Override
    @Transactional
    public void reportLostBook(Long borrowId) {
        BorrowRecord borrowRecord = findById(borrowId)
                .orElseThrow(() -> new IllegalArgumentException("Borrow record not found"));
        // check if book is currently borrowed
        if (borrowRecord.getStatus() == BorrowStatus.LOST) {
            throw new IllegalArgumentException("Book is already reported lost");
        }
        // check if book is currently borrowed
        if (borrowRecord.getStatus() == BorrowStatus.RETURNED) {
            throw new IllegalArgumentException("Cannot mark returned book as lost");
        }

        // set borrow record status to LOST
        // set borrow record return date to now
        // set borrow record fine to LOST_BOOK_FINE
        borrowRecord.setStatus(BorrowStatus.LOST);
        borrowRecord.setReturnDate(LocalDateTime.now());
        borrowRecord.setFine(LibraryConstants.LOST_BOOK_FINE);
        borrowRecord.setActualReturnCondition("Book reported lost");

        // update book quantity
        Book book = borrowRecord.getBook();
        int currentQuantity = book.getQuantity();

        book.setQuantity(currentQuantity - 1);

        // if quantity is 0, set book status to OUT_OF_STOCK
        if (currentQuantity - 1 <= 0) {
            book.setAvailable(false);
            book.setBookStatus(BookStatus.OUT_OF_STOCK);
        }

        // update book
        bookService.update(book);
        // update borrow record
        borrowRecordRepository.update(borrowRecord);
    }

    @Override
    public int countCurrentBorrows() {
        return borrowRecordRepository.countCurrentBorrows();
    }

    @Override
    public int countOverdueBorrows() {
        return borrowRecordRepository.countOverdueBorrows();
    }

    @Override
    public int calculateTotalFinesThisMonth() {
        return borrowRecordRepository.calculateTotalFinesThisMonth();
    }

    @Override
    public Map<String, Integer> getBorrowTrendsLastSixMonths() {
        return borrowRecordRepository.getBorrowTrendsLastSixMonths();
    }

    @Override
    public Map<String, Integer> getMostPopularBooks(int limit) {
        return borrowRecordRepository.getMostPopularBooks(limit);
    }

    @Override
    public List<RecentActivity> getRecentActivities(int limit) {
        return borrowRecordRepository.getRecentActivities(limit);
    }

    @Override
    public List<Alert> getActiveAlerts() {
        return borrowRecordRepository.getActiveAlerts();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecord> searchBorrowRecords(String memberKeyword, BorrowStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        List<BorrowRecord> borrows = borrowRecordRepository.searchBorrowRecords(memberKeyword, status, startDate, endDate);
        borrows.forEach(borrow -> {
            Hibernate.initialize(borrow.getMember());
            Hibernate.initialize(borrow.getBook());
        });
        return borrows;
    }

    @Override
    public long countActiveBooksByMember(Long memberId) {
        return borrowRecordRepository.countActiveBooksByMember(memberId);
    }

    @Override
    public void update(BorrowRecord borrowRecord) {
        borrowRecordRepository.update(borrowRecord);
    }

    @Override
    public Optional<BorrowRecord> findByIdWithMember(Long id) {
        return borrowRecordRepository.findByIdWithMember(id);
    }

    @Override
    public PageResponse<BorrowRecord> searchBorrowRecords(String memberSearch, BorrowStatus status,
            LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        // TODO Auto-generated method stub
        return borrowRecordRepository.searchBorrowRecords(memberSearch, status, startDate, endDate, page, size);
    }
}
