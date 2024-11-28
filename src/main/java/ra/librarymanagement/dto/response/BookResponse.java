package ra.librarymanagement.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import ra.librarymanagement.model.book.BookStatus;

@Data
@Builder
public class BookResponse {
    private Long bookId;
    private String bookCode;
    private String title;
    private String author;
    private String category;
    private int quantity;
    private BookStatus status;
    private String coverImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
