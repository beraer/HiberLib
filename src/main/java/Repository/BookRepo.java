package Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import table.Book;

import java.util.List;
import java.util.Optional;

/**
 * Repository class for managing {@link Book} entities.
 * Provides methods for performing database operations related to books.
 */
public class BookRepo extends AbstractRepo<Book> {

    /**
     * Constructs a {@code BookRepo} with the specified {@link EntityManager}.
     *
     * @param entityManager the {@link EntityManager} to be used for database operations.
     */
    public BookRepo(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Retrieves all books, including their publishers and copies, with distinct results.
     *
     * @return a list of all books in the database.
     */
    @Override
    public List<Book> findAll() {
        return entityManager.createQuery(
                        "SELECT DISTINCT b FROM Book b " +
                                "LEFT JOIN FETCH b.publisher " +
                                "LEFT JOIN FETCH b.copies", Book.class)
                .getResultList();
    }

    /**
     * Finds a book by its ISBN.
     *
     * @param isbn the ISBN of the book to find.
     * @return an {@link Optional} containing the book if found, or empty otherwise.
     */
    public Optional<Book> findByIsbn(String isbn) {
        TypedQuery<Book> query = entityManager.createQuery(
                "SELECT DISTINCT b FROM Book b " +
                        "LEFT JOIN FETCH b.publisher " +
                        "LEFT JOIN FETCH b.copies " +
                        "WHERE b.isbn = :isbn", Book.class);
        query.setParameter("isbn", isbn);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Finds books by their title, with a case-insensitive search.
     *
     * @param title the title or partial title of the books to find.
     * @return a list of books matching the given title.
     */
    public List<Book> findByTitle(String title) {
        TypedQuery<Book> query = entityManager.createQuery(
                "SELECT DISTINCT b FROM Book b " +
                        "LEFT JOIN FETCH b.publisher " +
                        "LEFT JOIN FETCH b.copies " +
                        "WHERE LOWER(b.title) LIKE LOWER(:title)", Book.class);
        query.setParameter("title", "%" + title + "%");
        return query.getResultList();
    }

    /**
     * Retrieves all books that have at least one available copy.
     *
     * @return a list of books with available copies.
     */
    public List<Book> findAvailableBooks() {
        return entityManager.createQuery(
                        "SELECT DISTINCT b FROM Book b " +
                                "LEFT JOIN FETCH b.publisher " +
                                "JOIN FETCH b.copies c " +
                                "WHERE c.status = 'Available'", Book.class)
                .getResultList();
    }

    /**
     * Retrieves all books ordered by their title.
     *
     * @return a list of books ordered alphabetically by title.
     */
    public List<Book> findAllOrderByTitle() {
        return entityManager.createQuery(
                        "SELECT DISTINCT b FROM Book b " +
                                "LEFT JOIN FETCH b.publisher " +
                                "LEFT JOIN FETCH b.copies " +
                                "ORDER BY b.title", Book.class)
                .getResultList();
    }

    /**
     * Retrieves all books with at least one available copy.
     *
     * @return a list of books with available copies.
     */
    public List<Book> findBooksWithAvailableCopies() {
        return entityManager.createQuery(
                        "SELECT DISTINCT b FROM Book b " +
                                "LEFT JOIN FETCH b.publisher " +
                                "JOIN FETCH b.copies c " +
                                "WHERE c.status = 'Available'", Book.class)
                .getResultList();
    }

    /**
     * Saves a book along with its publisher. If the publisher is new, it is also persisted.
     *
     * @param book the book to save.
     * @return the saved book with updated relationships.
     */
    public Book saveWithPublisher(Book book) {
        entityManager.getTransaction().begin();
        try {
            if (book.getPublisher() != null && book.getPublisher().getId() == null) {
                entityManager.persist(book.getPublisher());
            }

            if (book.getId() == null) {
                entityManager.persist(book);
            } else {
                book = entityManager.merge(book);
            }

            entityManager.getTransaction().commit();
            entityManager.refresh(book);
            return book;

        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }
}
