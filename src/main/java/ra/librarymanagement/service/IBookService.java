package ra.librarymanagement.service;

import ra.librarymanagement.model.book.Book;
import ra.librarymanagement.model.book.BookStatus;

import java.util.List;
import java.util.Optional;

public interface IBookService {
    List<Book> findAll();

    Optional<Book> findById(Long id);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByTitleContaining(String title);

    List<Book> findByAuthorContaining(String author);

    List<Book> findByStatus(BookStatus status);

    List<Book> findByCategory(String category);

    Book save(Book book);

    Book update(Book book);

    boolean delete(Long id);

    boolean isAvailableForBorrow(Long bookId);

    void updateQuantity(Long bookId, Integer quantity);

    long getAvailableCopies(Long bookId);

}
