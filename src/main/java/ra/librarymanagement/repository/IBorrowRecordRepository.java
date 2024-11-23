package ra.librarymanagement.repository;

import ra.librarymanagement.model.BorrowRecord.BorrowRecord;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;
import ra.librarymanagement.model.statistic.Alert;
import ra.librarymanagement.model.statistic.RecentActivity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IBorrowRecordRepository {
    List<BorrowRecord> findAll();

    Optional<BorrowRecord> findById(Long id);

    List<BorrowRecord> findByMemberId(Long memberId);

    List<BorrowRecord> findByBookId(Long bookId);

    List<BorrowRecord> findByStatus(BorrowStatus status);

    List<BorrowRecord> findOverdueRecords();

    void save(BorrowRecord borrowRecord);

    void update(BorrowRecord borrowRecord);

    void delete(Long id);

    List<BorrowRecord> findByBorrowDateBetween(LocalDateTime start, LocalDateTime end);

    long countActiveBooksByMember(Long memberId);

    int countCurrentBorrows();

    int countOverdueBorrows();

    int calculateTotalFinesThisMonth();

    Map<String, Integer> getBorrowTrendsLastSixMonths();

    Map<String, Integer> getMostPopularBooks(int limit);

    List<RecentActivity> getRecentActivities(int limit);

    List<Alert> getActiveAlerts();
}
