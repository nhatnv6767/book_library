package ra.librarymanagement.repository;

import ra.librarymanagement.model.member.Member;
import ra.librarymanagement.model.member.MemberStatus;
import ra.librarymanagement.model.member.MemberType;

import java.util.List;
import java.util.Optional;

public interface IMemberRepository {
    List<Member> findAll();

    Optional<Member> findById(Long id);

    Optional<Member> findByMemberCode(String memberCode);

    Optional<Member> findByEmail(String email);

    List<Member> findByFullNameContaining(String fullName);

    List<Member> findByStatus(MemberStatus status);

    List<Member> findByMemberType(MemberType memberType);

    void save(Member member);

    void update(Member member);

    void delete(Member member);

    boolean existsByMemberCode(String memberCode);

    boolean existsByEmail(String email);

    List<Member> findActiveMembers();
}
