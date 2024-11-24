package ra.librarymanagement.repository.imp;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.librarymanagement.model.BorrowRecord.BorrowRecord;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;
import ra.librarymanagement.model.book.Book;
import ra.librarymanagement.model.member.Member;
import ra.librarymanagement.model.statistic.Alert;
import ra.librarymanagement.model.statistic.RecentActivity;
import ra.librarymanagement.repository.IBorrowRecordRepository;
import ra.librarymanagement.util.CriteriaUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
@Transactional
public class BorrowRecordRepositoryImp implements IBorrowRecordRepository {

    private static final Logger logger = LoggerFactory.getLogger(BorrowRecordRepositoryImp.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<BorrowRecord> findAll() {
        try{
            CriteriaUtil.Result<BorrowRecord> result = CriteriaUtil.getResult(entityManager, BorrowRecord.class);

            result.root.fetch("member", JoinType.LEFT);
            result.root.fetch("book", JoinType.LEFT);

            result.query.orderBy(result.cb.desc(result.root.get("borrowDate")));
            List<BorrowRecord> records = entityManager.createQuery(result.query).getResultList();

            records.forEach(record -> {
                if(record.getFine() == null){
                    record.setFine(BigDecimal.ZERO);
                }
            });


            return records;
        } catch (Exception e) {
            logger.error("Error finding all borrow records: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<BorrowRecord> findById(Long id) {
        BorrowRecord record = entityManager.find(BorrowRecord.class, id);
        return Optional.ofNullable(record);
    }

    @Override
    public List<BorrowRecord> findByMemberId(Long memberId) {
        CriteriaUtil.Result<BorrowRecord> result = CriteriaUtil.getResult(entityManager, BorrowRecord.class);
        // that means select * from borrow_records where member_id = memberId
        result.query.where(result.cb.equal(result.root.get("member").get("memberId"), memberId));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public List<BorrowRecord> findByBookId(Long bookId) {
        CriteriaUtil.Result<BorrowRecord> result = CriteriaUtil.getResult(entityManager, BorrowRecord.class);
        result.query.where(result.cb.equal(result.root.get("book").get("bookId"), bookId));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public List<BorrowRecord> findByStatus(BorrowStatus status) {
        CriteriaUtil.Result<BorrowRecord> result = CriteriaUtil.getResult(entityManager, BorrowRecord.class);
        result.query.where(result.cb.equal(result.root.get("status"), status));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public List<BorrowRecord> findOverdueRecords() {
        try {
            CriteriaUtil.Result<BorrowRecord> result = CriteriaUtil.getResult(entityManager, BorrowRecord.class);
            Predicate statusPredicate = result.cb.equal(result.root.get("status"), BorrowStatus.BORROWING);
            Predicate dueDatePredicate = result.cb.lessThan(result.root.get("dueDate"), LocalDateTime.now());

            result.query.where(result.cb.and(statusPredicate, dueDatePredicate));

            return entityManager.createQuery(result.query).getResultList();
        } catch (Exception e) {
            // TODO: handle exception
            logger.error("Error finding overdue records: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public void save(BorrowRecord borrowRecord) {
        entityManager.persist(borrowRecord);
    }

    @Override
    @Transactional
    public void update(BorrowRecord borrowRecord) {
        entityManager.merge(borrowRecord);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BorrowRecord record = entityManager.find(BorrowRecord.class, id);
        if (record != null) {
            entityManager.remove(record);
        }
    }

    @Override
    public List<BorrowRecord> findByBorrowDateBetween(LocalDateTime start, LocalDateTime end) {
        CriteriaUtil.Result<BorrowRecord> result = CriteriaUtil.getResult(entityManager, BorrowRecord.class);
        // that means select * from borrow_records where borrow_date between start and end
        result.query.where(result.cb.between(result.root.get("borrowDate"), start, end));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public long countActiveBooksByMember(Long memberId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<BorrowRecord> root = query.from(BorrowRecord.class);

        // that means select count(*) from borrow_records where member_id = memberId and status = BORROWING
        Predicate memberPredicate = cb.equal(root.get("member").get("memberId"), memberId);
        Predicate statusPredicate = cb.equal(root.get("status"), BorrowStatus.BORROWING);

        query.select(cb.count(root)).where(cb.and(memberPredicate, statusPredicate));
        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public int countCurrentBorrows() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<BorrowRecord> root = query.from(BorrowRecord.class);

        // that means select count(*) from borrow_records where status = BORROWING
        query.select(cb.count(root)).where(cb.equal(root.get("status"), BorrowStatus.BORROWING));
        return entityManager.createQuery(query).getSingleResult().intValue();
    }

    @Override
    public int countOverdueBorrows() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<BorrowRecord> root = query.from(BorrowRecord.class);

        // that means select count(*) from borrow_records where status = BORROWING and due_date < now
        Predicate statusPredicate = cb.equal(root.get("status"), BorrowStatus.BORROWING);
        Predicate dueDatePredicate = cb.lessThan(root.get("dueDate"), LocalDateTime.now());

        query.select(cb.count(root)).where(cb.and(statusPredicate, dueDatePredicate));
        return entityManager.createQuery(query).getSingleResult().intValue();
    }

    @Override
    public int calculateTotalFinesThisMonth() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        Root<BorrowRecord> root = query.from(BorrowRecord.class);

        // that means select sum(fine) from borrow_records where status = RETURNED and return_date between start and end
        Predicate statusPredicate = cb.equal(root.get("status"), BorrowStatus.RETURNED);
        Predicate returnDatePredicate = cb.between(root.get("returnDate"), LocalDateTime.now().withDayOfMonth(1), LocalDateTime.now());

        query.select(cb.sum(root.<BigDecimal>get("fine"))).where(cb.and(statusPredicate, returnDatePredicate));
        BigDecimal result = entityManager.createQuery(query).getSingleResult();
        return result != null ? result.intValue() : 0;
    }

    @Override
    public Map<String, Integer> getBorrowTrendsLastSixMonths() {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
            Root<BorrowRecord> root = query.from(BorrowRecord.class);

            // get six months range
            LocalDateTime endDate = LocalDateTime.now()
                    .withHour(23).withMinute(59).withSecond(59);
            LocalDateTime startDate = endDate.minusMonths(6)
                    .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

            // SELECT MONTH(borrow_date) as month, COUNT(*) as count
            // FROM borrow_records
            // WHERE borrow_date BETWEEN start_date AND end_date
            // GROUP BY MONTH(borrow_date)
            // ORDER BY MONTH(borrow_date)
            query.multiselect(
                    cb.function("MONTH", Integer.class, root.get("borrowDate")),
                    cb.function("YEAR", Integer.class, root.get("borrowDate")),
                    cb.count(root)
            ).where(
                    cb.between(root.get("borrowDate"), startDate, endDate)
            ).groupBy(
                    cb.function("MONTH", Integer.class, root.get("borrowDate")),
                    cb.function("YEAR", Integer.class, root.get("borrowDate"))
            ).orderBy(
                    cb.asc(cb.function("YEAR", Integer.class, root.get("borrowDate"))),
                    cb.asc(cb.function("MONTH", Integer.class, root.get("borrowDate")))
            );

            List<Object[]> results = entityManager.createQuery(query).getResultList();

            // convert result to map
            Map<String, Integer> trends = new LinkedHashMap<>(); // using LinkedHashMap to keep order

            // create map with all 6 months, default count = 0
            for (int i = 5; i >= 0; i--) {
                LocalDateTime month = endDate.minusMonths(i);
                String monthKey = month.format(DateTimeFormatter.ofPattern("MM/yyyy"));
                trends.put(monthKey, 0);
            }

            // update count for months that have data
            for (Object[] result : results) {
                int month = (Integer) result[0];
                int year = (Integer) result[1];
                Long count = (Long) result[2];

                String monthKey = String.format("%02d/%d", month, year);
                trends.put(monthKey, count.intValue());
            }

            logger.info("Borrow trends for last 6 months: {}", trends);
            return trends;

        } catch (Exception e) {
            logger.error("Error getting borrow trends: " + e.getMessage(), e);
            return new LinkedHashMap<>(); // return empty map
        }
    }

    @Override
    public Map<String, Integer> getMostPopularBooks(int limit) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
            Root<BorrowRecord> root = query.from(BorrowRecord.class);
            // SELECT b.title, COUNT(*) as borrow_count
            // FROM borrow_records br
            // JOIN books b ON br.book_id = b.book_id
            // GROUP BY b.book_id, b.title
            // ORDER BY borrow_count DESC
            // LIMIT :limit
            query.multiselect(
                    root.get("book").get("title"),
                    cb.count(root)
            ).groupBy(
                    root.get("book").get("bookId"),
                    root.get("book").get("title")
            ).orderBy(
                    cb.desc(cb.count(root))
            );

            // convert result to map
            Map<String, Integer> popularBooks = new LinkedHashMap<>(); // using LinkedHashMap to keep order

            List<Object[]> results = entityManager.createQuery(query)
                    .setMaxResults(limit)
                    .getResultList();

            for (Object[] result : results) {
                String bookTitle = (String) result[0];
                Long borrowCount = (Long) result[1];
                popularBooks.put(bookTitle, borrowCount.intValue());
            }

            logger.info("Most popular books (limit {}): {}", limit, popularBooks);
            return popularBooks;

        } catch (Exception e) {
            logger.error("Error getting most popular books: " + e.getMessage(), e);
            return new LinkedHashMap<>(); // return empty map
        }

    }

    @Override
    public List<RecentActivity> getRecentActivities(int limit) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
            Root<BorrowRecord> root = query.from(BorrowRecord.class);

            Join<BorrowRecord, Member> memberJoin = root.join("member");
            Join<BorrowRecord, Book> bookJoin = root.join("book");

            // SELECT br.status, br.borrow_date, br.return_date,
            // m.full_name, b.title
            // FROM borrow_records br
            // JOIN members m ON br.member_id = m.member_id
            // JOIN books b ON br.book_id = b.book_id
            // ORDER BY COALESCE(br.return_date, br.borrow_date) DESC
            // LIMIT :limit
            query.multiselect(
                    root.get("borrowId"),
                    root.get("status"),
                    root.get("borrowDate"),
                    root.get("returnDate"),
                    memberJoin.get("fullName"),
                    bookJoin.get("title")
            ).orderBy(
                    cb.desc(cb.coalesce(root.get("returnDate"), root.get("borrowDate")))
            );

            List<Object[]> results = entityManager.createQuery(query)
                    .setMaxResults(limit)
                    .getResultList();

            List<RecentActivity> activities = new ArrayList<>();

            for (Object[] result : results) {
                Long borrowId = (Long) result[0];
                String status = (String) result[1];
                LocalDateTime borrowDate = (LocalDateTime) result[2];
                LocalDateTime returnDate = (LocalDateTime) result[3];
                String memberName = (String) result[4];
                String bookTitle = (String) result[5];

                BorrowStatus borrowStatus = BorrowStatus.valueOf(status);

                RecentActivity activity = new RecentActivity(
                        borrowId,
                        borrowStatus,
                        bookTitle,
                        memberName,
                        borrowDate,
                        returnDate
                );
                activities.add(activity);
            }

            logger.info("Recent activities (limit {}): {}", limit, activities);
            return activities;

        } catch (Exception e) {
            logger.error("Error getting recent activities: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Alert> getActiveAlerts() {
        try {
            List<Alert> alerts = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            // 1. find due soon books (due in 3 days)
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> dueSoonQuery = cb.createQuery(Object[].class);
            Root<BorrowRecord> dueSoonRoot = dueSoonQuery.from(BorrowRecord.class);

            LocalDateTime threeDaysFromNow = now.plusDays(3);
            Predicate dueSoonPredicate = cb.and(
                    cb.equal(dueSoonRoot.get("status"), BorrowStatus.BORROWING),
                    cb.between(
                            dueSoonRoot.get("dueDate"),
                            now,
                            threeDaysFromNow
                    )
            );

            dueSoonQuery.multiselect(
                            dueSoonRoot.get("book").get("title"),
                            dueSoonRoot.get("member").get("fullName"),
                            dueSoonRoot.get("dueDate")
                    ).where(dueSoonPredicate)
                    .orderBy(cb.asc(dueSoonRoot.get("dueDate")));

            List<Object[]> dueSoonResults = entityManager.createQuery(dueSoonQuery).getResultList();

            // create alert for due soon books
            for (Object[] record : dueSoonResults) {
                String bookTitle = (String) record[0];
                String memberName = (String) record[1];
                LocalDateTime dueDate = (LocalDateTime) record[2];

                Alert alert = Alert.builder()
                        .type("DUE_SOON")
                        .message(String.format("Book '%s' borrowed by %s is due in %d days",
                                bookTitle, memberName,
                                ChronoUnit.DAYS.between(now, dueDate)))
                        .dueDate(dueDate)
                        .build();

                alerts.add(alert);
            }

            // 2. find books that are overdue
            CriteriaQuery<Object[]> overdueQuery = cb.createQuery(Object[].class);
            Root<BorrowRecord> overdueRoot = overdueQuery.from(BorrowRecord.class);

            Predicate overduePredicate = cb.and(
                    cb.equal(overdueRoot.get("status"), BorrowStatus.BORROWING),
                    cb.lessThan(overdueRoot.get("dueDate"), now)
            );

            overdueQuery.multiselect(
                            overdueRoot.get("book").get("title"),
                            overdueRoot.get("member").get("fullName"),
                            overdueRoot.get("dueDate")
                    ).where(overduePredicate)
                    .orderBy(cb.asc(overdueRoot.get("dueDate")));

            List<Object[]> overdueResults = entityManager.createQuery(overdueQuery).getResultList();

            // create alert for overdue books
            for (Object[] record : overdueResults) {
                String bookTitle = (String) record[0];
                String memberName = (String) record[1];
                LocalDateTime dueDate = (LocalDateTime) record[2];

                Alert alert = Alert.builder()
                        .type("OVERDUE")
                        .message(String.format("Book '%s' borrowed by %s is overdue by %d days",
                                bookTitle, memberName,
                                ChronoUnit.DAYS.between(dueDate, now)))
                        .dueDate(dueDate)
                        .build();

                alerts.add(alert);
            }

            // sort alerts by due date
            alerts.sort((a1, a2) -> a1.getDueDate().compareTo(a2.getDueDate()));

            logger.info("Found {} active alerts", alerts.size());
            return alerts;

        } catch (Exception e) {
            logger.error("Error getting active alerts: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
