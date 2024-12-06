package ra.librarymanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BorrowResponse {
    private Long id;
    private String borrowCode;
    private MemberResponse member;
    private BookResponse book;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private BorrowStatus status;
    private int extensionCount;
    private BigDecimal fine;
    private String note;
}
