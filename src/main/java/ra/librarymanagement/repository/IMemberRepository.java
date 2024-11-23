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

    List<Member> searchMembers(String keyword, MemberType memberType, MemberStatus status);

    void save(Member member);

    void update(Member member);

    void delete(Long id);

    boolean existsByMemberCode(String memberCode);

    boolean existsByEmail(String email);

    List<Member> findActiveMembers();

    long countActiveBooksByMember(Long memberId);

    int countActiveMembers();

    int countNewMembersThisMonth();

    Optional<Member> findByIdWithBorrowRecords(Long id);
}
