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
import javax.persistence.criteria.Root;
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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Book> root = query.from(Book.class);
        
        return false;
    }

    @Override
    public List<Book> findAvailableBooks() {
        return List.of();
    }

    @Override
    public void updateQuantity(Long bookId, Integer quantity) {

    }
}
