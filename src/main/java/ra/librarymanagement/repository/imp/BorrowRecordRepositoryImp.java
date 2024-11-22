package ra.librarymanagement.repository.imp;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.librarymanagement.model.BorrowRecord.BorrowRecord;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;
import ra.librarymanagement.repository.IBorrowRecordRepository;
import ra.librarymanagement.util.CriteriaUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class BorrowRecordRepositoryImp implements IBorrowRecordRepository {

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
        return List.of();
    }

    @Override
    public List<BorrowRecord> findByStatus(BorrowStatus status) {
        return List.of();
    }

    @Override
    public List<BorrowRecord> findOverdueRecords() {
        return List.of();
    }

    @Override
    public void save(BorrowRecord borrowRecord) {

    }

    @Override
    public void update(BorrowRecord borrowRecord) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<BorrowRecord> findByBorrowDateBetween(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    @Override
    public long countActiveBooksByMember(Long memberId) {
        return 0;
    }
}
