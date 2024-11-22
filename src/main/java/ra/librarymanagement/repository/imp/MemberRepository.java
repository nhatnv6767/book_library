package ra.librarymanagement.repository.imp;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.librarymanagement.model.member.Member;
import ra.librarymanagement.model.member.MemberStatus;
import ra.librarymanagement.model.member.MemberType;
import ra.librarymanagement.repository.IMemberRepository;
import ra.librarymanagement.util.CriteriaUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class MemberRepository implements IMemberRepository {

    @PersistenceContext
    private EntityManager entityManager;

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
    public void update(Member member) {
        entityManager.merge(member);
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
}
