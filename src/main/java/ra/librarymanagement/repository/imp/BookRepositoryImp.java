package ra.librarymanagement.repository.imp;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.librarymanagement.model.book.Book;
import ra.librarymanagement.model.book.BookStatus;
import ra.librarymanagement.repository.IBookRepository;
import ra.librarymanagement.util.CriteriaUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class BookRepositoryImp implements IBookRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Book> findAll() {
        CriteriaUtil.Result<Book> result = CriteriaUtil.getResult(entityManager, Book.class);
        result.query.select(result.root);
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public Optional<Book> findById(Long id) {
        Book book = entityManager.find(Book.class, id);
        return Optional.ofNullable(book);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        CriteriaUtil.Result<Book> result = CriteriaUtil.getResult(entityManager, Book.class);
        result.query.where(result.cb.equal(result.root.get("isbn"), isbn));
        try {
            return Optional.of(entityManager.createQuery(result.query).getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findByTitleContaining(String title) {
        CriteriaUtil.Result<Book> result = CriteriaUtil.getResult(entityManager, Book.class);
        result.query.where(result.cb.like(result.root.get("title"), "%" + title + "%"));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public List<Book> findByAuthorContaining(String author) {
        CriteriaUtil.Result<Book> result = CriteriaUtil.getResult(entityManager, Book.class);
        result.query.where(result.cb.like(result.root.get("author"), "%" + author + "%"));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public List<Book> findByStatus(BookStatus status) {
        CriteriaUtil.Result<Book> result = CriteriaUtil.getResult(entityManager, Book.class);
        result.query.where(result.cb.equal(result.root.get("bookStatus"), status));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public List<Book> findByCategory(String category) {
        CriteriaUtil.Result<Book> result = CriteriaUtil.getResult(entityManager, Book.class);
        result.query.where(result.cb.equal(result.root.get("category"), category));
        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    @Transactional
    public void save(Book book) {
        entityManager.persist(book);
    }

    @Override
    @Transactional
    public void update(Book book) {
        entityManager.merge(book);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = entityManager.find(Book.class, id);
        if (book != null) {
            entityManager.remove(book);
        }
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        // Create a CriteriaBuilder
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        // Return the number of books with the given ISBN
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        // SELECT COUNT(*) FROM books
        Root<Book> root = query.from(Book.class);
        // SELECT COUNT(*) FROM books WHERE isbn = :isbn
        query.select(cb.count(root)).where(cb.equal(root.get("isbn"), isbn));
        // If the number of books with the given ISBN is greater than 0, return true
        return entityManager.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public List<Book> findAvailableBooks() {
        CriteriaUtil.Result<Book> result = CriteriaUtil.getResult(entityManager, Book.class);

        // SELECT * FROM books WHERE available = true
        Predicate availablePredicate = result.cb.equal(result.root.get("available"), true);
        // SELECT * FROM books WHERE quantity > 0
        Predicate quantityPredicate = result.cb.greaterThan(result.root.get("quantity"), 0);
        // SELECT * FROM books WHERE status = 'AVAILABLE'
        Predicate statusPredicate = result.cb.equal(result.root.get("bookStatus"), BookStatus.AVAILABLE);
        // SELECT * FROM books WHERE available = true AND quantity > 0 AND status = 'AVAILABLE
        result.query.where(result.cb.and(availablePredicate, quantityPredicate, statusPredicate));

        return entityManager.createQuery(result.query).getResultList();
    }

    @Override
    public Long countAvailableBooks() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Book> root = query.from(Book.class);
        // SELECT COUNT(*) FROM books WHERE available = true AND quantity > 0 AND status = 'AVAILABLE'
        query.select(cb.count(root)).where(
                cb.and(
                        cb.equal(root.get("available"), true),
                        cb.greaterThan(root.get("quantity"), 0)
                        // cb.equal(root.get("status"), BookStatus.AVAILABLE)
                )
        );
        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public void updateQuantity(Long bookId, Integer quantity) {
        Book book = entityManager.find(Book.class, bookId);
        if (book != null) {
            book.setQuantity(quantity);
            book.setAvailable(quantity > 0);
            book.setBookStatus(quantity > 0 ? BookStatus.AVAILABLE : BookStatus.OUT_OF_STOCK);
            entityManager.merge(book);
        }
    }

//    @Override
//    public int countTotalBooks() {
//        CriteriaUtil.Result<Long> result = CriteriaUtil.getResult(entityManager, Long.class);
//        result.query.select(result.cb.count(result.root));
//        return entityManager.createQuery(result.query).getSingleResult().intValue();
//    }


    @Override
    public Long countTotalBooks() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Book> root = query.from(Book.class);

        query.select(cb.count(root));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    public List<Book> searchBooks(String keyword, BookStatus status, String category) {
        // TODO Auto-generated method stub
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Book> query = cb.createQuery(Book.class);
            Root<Book> root = query.from(Book.class);

            List<Predicate> predicates = new ArrayList<>();

            // Search by keyword (title, author, isbn)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKeyword = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), searchKeyword),
                        cb.like(cb.lower(root.get("author")), searchKeyword),
                        cb.like(cb.lower(root.get("isbn")), searchKeyword)
                ));
            }

            // Filter by status
            if (status != null) {
                predicates.add(cb.equal(root.get("bookStatus"), status));
            }

            // Filter by category
            if (category != null && !category.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (!predicates.isEmpty()) {
                query.where(predicates.toArray(new Predicate[0]));
            }

            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error searching books: " + e.getMessage());
        }
    }

}
