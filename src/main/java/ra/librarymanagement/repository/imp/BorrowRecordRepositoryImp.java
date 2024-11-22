package ra.librarymanagement.repository.imp;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.librarymanagement.model.BorrowRecord.BorrowRecord;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;
import ra.librarymanagement.repository.IBorrowRecordRepository;
import ra.librarymanagement.util.CriteriaUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        CriteriaUtil.Result<BorrowRecord> result = CriteriaUtil.getResult(entityManager, BorrowRecord.class);
        result.query.select(result.root);
        return entityManager.createQuery(result.query).getResultList();
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
}
