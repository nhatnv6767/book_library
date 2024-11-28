package ra.librarymanagement.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ra.librarymanagement.paging.PageResponse;
import ra.librarymanagement.model.book.Book;
import ra.librarymanagement.model.book.BookStatus;
import ra.librarymanagement.repository.IBookRepository;
import ra.librarymanagement.service.IBookService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookServiceImp implements IBookService {

    //    @Autowired
//    private IBookRepository bookRepository;
    private final IBookRepository bookRepository;

    @Autowired
    private BookServiceImp(IBookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public List<Book> findByTitleContaining(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    @Override
    public List<Book> findByAuthorContaining(String author) {
        return bookRepository.findByAuthorContaining(author);
    }

    @Override
    public List<Book> findByStatus(BookStatus status) {
        return bookRepository.findByStatus(status);
    }

    @Override
    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategory(category);
    }

    @Override
    @Transactional
    public Book save(Book book) {

        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        // set default values
        book.setAvailable(true);
        book.setBookStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        return book;
    }

    @Override
    @Transactional
    public Book update(Book book) {
        Book existingBook = bookRepository.findById(book.getBookId()).orElseThrow(() -> new IllegalArgumentException("Book with ID " + book.getBookId() + " does not exist"));

        // check ISBN if changed
        // if changed, check if new ISBN already exists
        // if exists, throw exception
        if (!existingBook.getIsbn().equals(book.getIsbn()) && bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists");
        }

        // update status based on quantity
        // if quantity > 0, set status to AVAILABLE
        book.setAvailable(book.getQuantity() > 0);
        book.setBookStatus(book.getQuantity() > 0 ? BookStatus.AVAILABLE : BookStatus.OUT_OF_STOCK);
        bookRepository.update(book);
        return book;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        try {
            bookRepository.delete(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isAvailableForBorrow(Long bookId) {
        return findById(bookId)
                .map(book -> book.isAvailable()
                        && book.getBookStatus() == BookStatus.AVAILABLE
                        && book.getQuantity() > 0)
                .orElse(false);
    }


    @Override
    @Transactional
    public void updateQuantity(Long bookId, Integer quantity) {
        Book book = findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book with ID " + bookId + " does not exist"));
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be greater than or equal to 0");
        }
        book.setQuantity(quantity);
        book.setAvailable(quantity > 0);
        book.setBookStatus(quantity > 0 ? BookStatus.AVAILABLE : BookStatus.OUT_OF_STOCK);
        bookRepository.update(book);
    }

    @Override
    public long getAvailableCopies(Long bookId) {
        return findById(bookId)
                .map(book -> book.isAvailable() ? book.getQuantity() : 0).orElse(0);
    }

    @Override
    public List<Book> findAvailableBooks() {
        return bookRepository.findAvailableBooks();
    }

    @Override
    public Long countTotalBooks() {
        return bookRepository.countTotalBooks();
    }

    @Override
    public Long countAvailableBooks() {
        return bookRepository.countAvailableBooks();
    }

    @Override
    public List<Book> searchBooks(String keyword, BookStatus status, String category) {
        // TODO Auto-generated method stub
        return bookRepository.searchBooks(
                keyword != null ? keyword.trim() : null,
                status,
                category != null ? category.trim() : null
        );
    }

    @Override
    public PageResponse<Book> searchBooks(String keyword, BookStatus status, String category, int page, int size) {
        return bookRepository.searchBooks(keyword, status, category, page, size);
    }

}
