package ra.librarymanagement.repository.imp;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.librarymanagement.paging.PageResponse;
import ra.librarymanagement.model.BorrowRecord.BorrowRecord;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;
import ra.librarymanagement.model.member.Member;
import ra.librarymanagement.model.member.MemberStatus;
import ra.librarymanagement.model.member.MemberType;
import ra.librarymanagement.repository.IMemberRepository;
import ra.librarymanagement.util.CriteriaUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Repository
@Transactional
public class MemberRepositoryImp implements IMemberRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(BorrowRecordRepositoryImp.class);

    @Override
    public List<Member> findAll() {
        CriteriaUtil.Result<Member> result = CriteriaUtil.getResult(entityManager, Member.class);
        result.query.select(result.root);
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = entityManager.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByMemberCode(String memberCode) {
        CriteriaUtil.Result<Member> result = CriteriaUtil.getResult(entityManager, Member.class);
        // that means select * from members where member_code = memberCode
        result.query.where(result.cb.equal(result.root.get("memberCode"), memberCode));
        try {
            return Optional.of(entityManager.createQuery(result.query).getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        CriteriaUtil.Result<Member> result = CriteriaUtil.getResult(entityManager, Member.class);
        result.query.where(result.cb.equal(result.root.get("email"), email));
        try {
            return Optional.of(entityManager.createQuery(result.query).getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Member> findByFullNameContaining(String fullName) {
        CriteriaUtil.Result<Member> result = CriteriaUtil.getResult(entityManager, Member.class);
        // that means select * from members where lower(full_name) like %fullName%
        result.query.where(result.cb.like(result.cb.lower(result.root.get("fullName")), "%" + fullName.toLowerCase() + "%"));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public List<Member> findByStatus(MemberStatus status) {
        CriteriaUtil.Result<Member> result = CriteriaUtil.getResult(entityManager, Member.class);
        result.query.where(result.cb.equal(result.root.get("status"), status));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public List<Member> findByMemberType(MemberType memberType) {
        CriteriaUtil.Result<Member> result = CriteriaUtil.getResult(entityManager, Member.class);
        result.query.where(result.cb.equal(result.root.get("memberType"), memberType));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    @Transactional
    public void save(Member member) {
        entityManager.persist(member);
    }

    @Override
    @Transactional
    public Member update(Member member) {
        try {

            if (member.getMemberId() == null) {
                throw new IllegalArgumentException("Member ID cannot be null");
            }

            Member existingMember = entityManager.find(Member.class, member.getMemberId());
            if (existingMember == null) {
                throw new IllegalArgumentException("Member ID cannot be null");
            }

            // Keep fields that cannot be changed or are automatically updated
            member.setMemberCode(existingMember.getMemberCode()); // unique, cannot be changed
            member.setCreatedAt(existingMember.getCreatedAt()); // @CreationTimestamp, cannot update
            member.setJoinDate(existingMember.getJoinDate()); // Join date cannot be changed
            member.setBorrowRecord(existingMember.getBorrowRecord()); // Keep relationship

            // Update timestamp
            member.setUpdatedAt(LocalDateTime.now());

            // Handle nullable fields
            if (member.getAddress() == null) {
                member.setAddress(existingMember.getAddress());
            }
            if (member.getDateOfBirth() == null) {
                member.setDateOfBirth(existingMember.getDateOfBirth());
            }
            if (member.getAvatar() == null) {
                member.setAvatar(existingMember.getAvatar());
            }
            if (member.getNote() == null) {
                member.setNote(existingMember.getNote());
            }
            if (member.getIdentityCard() == null) {
                member.setIdentityCard(existingMember.getIdentityCard());
            }
            if (member.getExpiryDate() == null) {
                member.setExpiryDate(existingMember.getExpiryDate());
            }

            Member updatedMember = entityManager.merge(member);
            entityManager.flush();
            return updatedMember;

        } catch (Exception e) {
            logger.error("Error updating member: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Member member = entityManager.find(Member.class, id);
        if (member != null) {
            entityManager.remove(member);
        }
    }

    @Override
    public boolean existsByMemberCode(String memberCode) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Member> root = query.from(Member.class);
        // that means select count(*) from members where member_code = memberCode
        query.select(cb.count(root)).where(cb.equal(root.get("memberCode"), memberCode));
        return entityManager.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Member> root = query.from(Member.class);
        // that means select count(*) from members where email = email
        query.select(cb.count(root)).where(cb.equal(root.get("email"), email));
        return entityManager.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public List<Member> findActiveMembers() {
        CriteriaUtil.Result<Member> result = CriteriaUtil.getResult(entityManager, Member.class);
        // that means select * from members where status = ACTIVE
        result.query.where(result.cb.equal(result.root.get("status"), MemberStatus.ACTIVE));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public long countActiveBooksByMember(Long memberId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<BorrowRecord> root = query.from(BorrowRecord.class);

        // that means select count(*) from borrow_records where member_id = memberId and status = BORROWING
        query.select(cb.count(root))
                .where(cb.and(
                        cb.equal(root.get("member").get("memberId"), memberId),
                        cb.equal(root.get("status"), BorrowStatus.BORROWING)
                ));
        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public int countActiveMembers() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Member> root = query.from(Member.class);
        // that means select count(*) from members where status = ACTIVE
        query.select(cb.count(root)).where(cb.equal(root.get("status"), MemberStatus.ACTIVE));
        return entityManager.createQuery(query).getSingleResult().intValue();
    }

    @Override
    public int countNewMembersThisMonth() {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Root<Member> root = query.from(Member.class);

            // Get first day of month and last day of month
            LocalDateTime firstDayOfMonth = LocalDateTime.now()
                    .withDayOfMonth(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0);

            LocalDateTime lastDayOfMonth = firstDayOfMonth
                    .plusMonths(1)
                    .minusSeconds(1);

            // SELECT COUNT(*) FROM members
            // WHERE status = 'ACTIVE'
            // AND join_date >= first_day_of_month
            // AND join_date <= last_day_of_month
            query.select(cb.count(root))
                    .where(cb.and(
                            cb.equal(root.get("status"), MemberStatus.ACTIVE),
                            cb.between(
                                    root.get("joinDate"),
                                    firstDayOfMonth,
                                    lastDayOfMonth
                            )
                    ));

            return entityManager.createQuery(query)
                    .getSingleResult()
                    .intValue();

        } catch (Exception e) {
            logger.error("Error counting new members this month: " + e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public List<Member> searchMembers(String keyword, MemberType memberType, MemberStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Member> query = cb.createQuery(Member.class);
        Root<Member> root = query.from(Member.class);

        // list of predicates
        List<Predicate> predicates = new ArrayList<>();

        // condition keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = "%" + keyword.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("memberCode")), searchKeyword),
                    cb.like(cb.lower(root.get("email")), searchKeyword),
                    cb.like(cb.lower(root.get("fullName")), searchKeyword)
            ));
        }

        // condition member type
        if (memberType != null) {
            predicates.add(cb.equal(root.get("memberType"), memberType));
        }

        // condition status
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        // add all predicates to query
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Optional<Member> findByIdWithBorrowRecords(Long id) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);
            Root<Member> root = query.from(Member.class);

            // Fetch BorrowRecord eagerly
            Fetch<Member, BorrowRecord> borrowRecordFetch = root.fetch("borrowRecord", JoinType.LEFT);

            // Fetch BorrowRecord eagerly
            borrowRecordFetch.fetch("book", JoinType.LEFT);
            // root.fetch("borrowRecord", JoinType.LEFT);

            query.where(cb.equal(root.get("memberId"), id));

            return Optional.of(entityManager.createQuery(query)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public String getLastMemberCode() {
        // TODO Auto-generated method stub
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<String> query = cb.createQuery(String.class);
            Root<Member> root = query.from(Member.class);

            query.select(root.get("memberCode")).where(
                    cb.like(root.get("memberCode"), "MEM%")
            ).orderBy(cb.desc(root.get("memberCode")));

            return entityManager.createQuery(query).setMaxResults(1).getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public PageResponse<Member> searchMembers(String keyword, MemberType memberType, MemberStatus status, int page, int size) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Member> query = cb.createQuery(Member.class);
            Root<Member> root = query.from(Member.class);
            List<Predicate> predicates = buildSearchPredicates(keyword, memberType, status, cb, root);
            if (!predicates.isEmpty()) {
                // that means select * from members where keyword and memberType and status
                query.where(predicates.toArray(new Predicate[0]));
            }

            query.orderBy(cb.asc(root.get("memberCode")));

            // count query
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Member> countRoot = countQuery.from(Member.class);
            List<Predicate> countPredicates = buildSearchPredicates(keyword, memberType, status, cb, countRoot);
            countQuery.select(cb.count(countRoot));
            if (!countPredicates.isEmpty()) {
                countQuery.where(countPredicates.toArray(new Predicate[0]));
            }

            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

            // get paginated results
            // that means select * from members where keyword and memberType and status
            TypedQuery<Member> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult(page * size);
            typedQuery.setMaxResults(size);
            List<Member> members = typedQuery.getResultList();

            return new PageResponse<>(members, page, size, totalElements);
        } catch (Exception e) {
            throw new RuntimeException("Error searching members: " + e.getMessage(), e);
        }
    }

    private List<Predicate> buildSearchPredicates(String keyword, MemberType memberType, MemberStatus status, CriteriaBuilder cb, Root<Member> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = "%" + keyword.toLowerCase() + "%";
            predicates.add(cb.or(
                    // that means select * from members where lower(member_code) like searchKeyword
                    cb.like(cb.lower(root.get("memberCode")), searchKeyword),
                    cb.like(cb.lower(root.get("email")), searchKeyword),
                    cb.like(cb.lower(root.get("fullName")), searchKeyword)
            ));
        }

        // that means select * from members where member_type = memberType
        if (memberType != null) {
            predicates.add(cb.equal(root.get("memberType"), memberType));
        }

        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        return predicates;
    }
}
