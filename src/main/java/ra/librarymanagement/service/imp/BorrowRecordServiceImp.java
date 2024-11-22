package ra.librarymanagement.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.librarymanagement.model.BorrowRecord.BorrowRecord;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;
import ra.librarymanagement.model.book.Book;
import ra.librarymanagement.model.member.Member;
import ra.librarymanagement.repository.IBorrowRecordRepository;
import ra.librarymanagement.service.IBookService;
import ra.librarymanagement.service.IBorrowRecordService;
import ra.librarymanagement.service.IMemberService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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

    private static final int DEFAULT_BORROW_DAYS = 14;
    private static final int MAX_EXTENSIONS = 2;
    private static final BigDecimal DAILY_FINE = new BigDecimal("1000"); // 1000 VND / day
    private static final BigDecimal LOST_BOOK_FINE = new BigDecimal("100000"); // 100000 VND

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
        borrowRecord.setDueDate(LocalDateTime.now().plusDays(DEFAULT_BORROW_DAYS));
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
            BigDecimal fine = DAILY_FINE.multiply(BigDecimal.valueOf(daysLate));
            borrowRecord.setFine(fine);
            borrowRecord.setStatus(BorrowStatus.OVERDUE);
            borrowRecordRepository.update(borrowRecord);
        }
    }

    @Override
    public List<BorrowRecord> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return new ArrayList<>();
    }

    @Override
    public List<BorrowRecord> findActiveBorrowsByMember(Long memberId) {
        return new ArrayList<>();
    }

    @Override
    public boolean extendBorrowPeriod(Long borrowId) {
        return false;
    }

    @Override
    public void reportLostBook(Long borrowId) {

    }
}
