package ra.librarymanagement.repository;

import ra.librarymanagement.model.book.Book;
import ra.librarymanagement.model.book.BookStatus;

import java.util.List;
import java.util.Optional;

import paging.PageResponse;

public interface IBookRepository {
    List<Book> findAll();

    Optional<Book> findById(Long id);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByTitleContaining(String title);

    List<Book> findByAuthorContaining(String author);

    List<Book> findByStatus(BookStatus status);

    List<Book> findByCategory(String category);

    void save(Book book);

    void update(Book book);

    void delete(Long id);

    boolean existsByIsbn(String isbn);

    List<Book> findAvailableBooks();

    void updateQuantity(Long bookId, Integer quantity);

    Long countTotalBooks();

    Long countAvailableBooks();

    List<Book> searchBooks(String keyword, BookStatus status, String category);

    PageResponse<Book> searchBooks(String keyword, BookStatus status, 
                                 String category, int page, int size);

}
