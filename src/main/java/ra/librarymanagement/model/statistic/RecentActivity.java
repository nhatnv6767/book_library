package ra.librarymanagement.model.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentActivity {
    private Long borrowId;
    private BorrowStatus status;
    private String bookTitle;
    private String memberName;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private String type;
    private String description;
    private LocalDateTime timestamp;

    public RecentActivity(Long borrowId, BorrowStatus status, String bookTitle,
                          String memberName, LocalDateTime borrowDate, LocalDateTime returnDate) {
        this.borrowId = borrowId;
        this.status = status;
        this.bookTitle = bookTitle;
        this.memberName = memberName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;


        if (returnDate == null) {
            this.type = "BORROW";
            this.description = memberName + " borrowed '" + bookTitle + "'";
            this.timestamp = borrowDate;
        } else {
            this.type = "RETURN";
            this.description = memberName + " returned '" + bookTitle + "'";
            this.timestamp = returnDate;
        }
    }
}
