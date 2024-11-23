package ra.librarymanagement.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.librarymanagement.model.member.Member;
import ra.librarymanagement.model.member.MemberStatus;
import ra.librarymanagement.model.member.MemberType;
import ra.librarymanagement.repository.IMemberRepository;
import ra.librarymanagement.service.IMemberService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberServiceImp implements IMemberService {

    private final IMemberRepository memberRepository;

    @Autowired
    public MemberServiceImp(IMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    public Optional<Member> findByMemberCode(String memberCode) {
        return memberRepository.findByMemberCode(memberCode);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public List<Member> findByFullNameContaining(String fullName) {
        return memberRepository.findByFullNameContaining(fullName);
    }

    @Override
    public List<Member> findByStatus(MemberStatus status) {
        return memberRepository.findByStatus(status);
    }

    @Override
    public List<Member> findByMemberType(MemberType memberType) {
        return memberRepository.findByMemberType(memberType);
    }

    @Override
    @Transactional
    public Member save(Member member) {
        // Check if member code already exists
        if (memberRepository.existsByMemberCode(member.getMemberCode())) {
            throw new IllegalArgumentException("Member code already exists");
        }
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        member.setJoinDate(LocalDateTime.now());
        member.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);

        return member;
    }

    @Override
    @Transactional
    public Member update(Member member) {
        // check if member exists
        Member existingMember = memberRepository
                .findById(member.getMemberId()).orElseThrow(() ->
                        new IllegalArgumentException("Member with ID " + member.getMemberId() + " does not exist"));
        // check member code if changed
        // if changed, check if new member code already exists
        // if exists, throw exception
        if (!existingMember.getMemberCode().equals(member.getMemberCode()) && memberRepository.existsByMemberCode(member.getMemberCode())) {
            throw new IllegalArgumentException("Member code already exists");
        }

        // check email if changed
        // if changed, check if new email already exists
        // if exists, throw exception
        if (!existingMember.getEmail().equals(member.getEmail()) && memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        memberRepository.save(member);

        return member;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        try {
            memberRepository.delete(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean canBorrowBooks(Long memberId) {
        // Check if member is active and has not reached borrow limit
        // Borrow limit is based on member type
        // Regular: 3 books
        // Student: 5 books
        // VIP: 10 books
        return findById(memberId)
                .map(member
                        -> member.getStatus() == MemberStatus.ACTIVE
                        && getActiveBorrowCount(memberId) < getMaxBorrowLimit(member.getMemberType())).orElse(false);
    }

    private int getMaxBorrowLimit(MemberType memberType) {
        switch (memberType) {
            case REGULAR:
                return 3;
            case STUDENT:
                return 5;
            case VIP:
                return 10;
            default:
                return 0;
        }
    }

    @Override
    public long getActiveBorrowCount(Long memberId) {
        return memberRepository.countActiveBooksByMember(memberId);
    }

    @Override
    @Transactional
    public void updateMemberStatus(Long memberId, MemberStatus status) {
        Member member = findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member with ID " + memberId + " does not exist"));
        member.setStatus(status);
        memberRepository.update(member);
    }

    @Override
    public int countActiveMembers() {
        return memberRepository.countActiveMembers();
    }

    @Override
    public int countNewMembersThisMonth() {
        return memberRepository.countNewMembersThisMonth();
    }
}
