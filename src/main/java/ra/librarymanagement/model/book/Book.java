package ra.librarymanagement.model.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ra.librarymanagement.model.BorrowRecord.BorrowRecord;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(exclude = "borrowRecords")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "isbn", nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "author", nullable = false, length = 100)
    private String author;

    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "available", nullable = false, columnDefinition = "boolean default true")
    private boolean available = true;

    @Column(name = "book_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "description", nullable = true, length = 1000)
    private String description;

    @Column(name = "cover_image", nullable = true, length = 255)
    private String coverImage;

    @Column(name = "publisher", nullable = true, length = 100)
    private String publisher;

    @Column(name = "edition", nullable = true, length = 50)
    private String edition;

    @Column(name = "price", nullable = true, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "language", nullable = true, length = 50)
    private String language;

    @OneToMany(mappedBy = "book")
    private List<BorrowRecord> borrowRecords;

}

