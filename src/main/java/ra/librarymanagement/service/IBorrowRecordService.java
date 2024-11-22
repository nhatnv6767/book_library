package ra.librarymanagement.service;

import ra.librarymanagement.model.BorrowRecord.BorrowRecord;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IBorrowRecordService {
    List<BorrowRecord> findAll();

    Optional<BorrowRecord> findById(Long id);

    List<BorrowRecord> findByMemberId(Long memberId);

    List<BorrowRecord> findByBookId(Long bookId);

    List<BorrowRecord> findByStatus(BorrowStatus status);

    List<BorrowRecord> findOverdueRecords();

    BorrowRecord borrowBook(Long memberId, Long bookId);

    BorrowRecord returnBook(Long borrowId);

    void calculateFine(Long borrowId);

    List<BorrowRecord> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<BorrowRecord> findActiveBorrowsByMember(Long memberId);

    boolean extendBorrowPeriod(Long borrowId);

    void reportLostBook(Long borrowId);
}
