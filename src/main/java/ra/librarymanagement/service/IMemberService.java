package ra.librarymanagement.service;

import ra.librarymanagement.model.member.Member;
import ra.librarymanagement.model.member.MemberStatus;
import ra.librarymanagement.model.member.MemberType;

import java.util.List;
import java.util.Optional;

public interface IMemberService {
    List<Member> findAll();

    Optional<Member> findById(Long id);

    Optional<Member> findByMemberCode(String memberCode);

    Optional<Member> findByEmail(String email);

    List<Member> findByFullNameContaining(String fullName);

    List<Member> findByStatus(MemberStatus status);

    List<Member> findByMemberType(MemberType memberType);

    Member save(Member member);

    Member update(Member member);

    boolean delete(Long id);

    boolean canBorrowBooks(Long memberId);

    long getActiveBorrowCount(Long memberId);

    void updateMemberStatus(Long memberId, MemberStatus status);

    int countActiveMembers();

    int countNewMembersThisMonth();

    List<Member> searchMembers(String keyword, MemberType memberType, MemberStatus status);

}
