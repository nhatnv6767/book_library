package ra.librarymanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import ra.librarymanagement.constants.LibraryConstants;
import ra.librarymanagement.model.member.MemberStatus;
import ra.librarymanagement.model.member.MemberType;

import java.time.LocalDateTime;

@Data
@Builder
public class MemberResponse {
    private Long memberId;
    private String memberCode;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private MemberType memberType;
    private MemberStatus status;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String note;
    
    // add fields for calculation
    private int maxBooksAllowed;
    private long activeBorrows;
    private boolean canBorrowMore;
    private boolean isExpired;
    
    public int getMaxBooksAllowed() {
        switch (memberType) {
            case REGULAR:
                return LibraryConstants.REGULAR_MEMBER_MAX_BOOKS;
            case VIP:
                return LibraryConstants.VIP_MEMBER_MAX_BOOKS;
            case STUDENT:
                return LibraryConstants.STUDENT_MEMBER_MAX_BOOKS;
            default:
                return 0;
        }
    }
    
    public boolean getCanBorrowMore() {
        return status == MemberStatus.ACTIVE && 
               !isExpired && 
               activeBorrows < getMaxBooksAllowed();
    }
    
    public boolean getIsExpired() {
        return expiryDate != null && 
               expiryDate.isBefore(LocalDateTime.now());
    }
}
