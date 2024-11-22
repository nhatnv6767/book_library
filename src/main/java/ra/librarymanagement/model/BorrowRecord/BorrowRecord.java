package ra.librarymanagement.model.BorrowRecord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ra.librarymanagement.model.book.Book;
import ra.librarymanagement.model.member.Member;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_records")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrow_record_id")
    private Long borrowId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    // @Column(name = "member")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    // @Column(name = "book")
    private Book book;

    @Column(name = "borrow_date", nullable = false)
    private LocalDateTime borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "return_date", nullable = true)
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BorrowStatus status;

    @Column(name = "fine", nullable = false, precision = 10, scale = 2)
    private BigDecimal fine = BigDecimal.ZERO;

    @Column(name = "note", nullable = true, length = 500)
    private String note;

    @Column(name = "returned_by", nullable = true, length = 100)
    private String returnedBy;

    @Column(name = "borrowed_by", nullable = true, length = 100)
    private String borrowedBy;

    @Column(name = "extension_count", nullable = false)
    private Integer extensionCount = 0;

    @Column(name = "actual_return_condition", nullable = true, length = 500)
    private String actualReturnCondition;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

}
