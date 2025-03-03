package Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import table.Book;
import table.Copy;

import java.util.List;
import java.util.Optional;

/**
 * Repository class for managing {@link Copy} entities.
 * Provides methods for performing database operations related to copies of books.
 */
public class CopyRepo extends AbstractRepo<Copy> {

    /**
     * Constructs a {@code CopyRepo} with the specified {@link EntityManager}.
     *
     * @param entityManager the {@link EntityManager} to be used for database operations.
     */
    public CopyRepo(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Finds a copy by its unique ID.
     *
     * @param id the ID of the copy.
     * @return an {@link Optional} containing the copy if found, otherwise empty.
     */
    @Override
    public Optional<Copy> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entityManager.find(Copy.class, id));
    }

    /**
     * Finds all copies associated with a specific book.
     *
     * @param book the {@link Book} whose copies are to be retrieved.
     * @return a list of copies for the specified book.
     */
    public List<Copy> findByBook(Book book) {
        TypedQuery<Copy> query = entityManager.createQuery(
                "SELECT c FROM Copy c WHERE c.book = :book", Copy.class);
        query.setParameter("book", book);
        return query.getResultList();
    }

    /**
     * Finds all available copies for a specific book.
     *
     * @param book the {@link Book} whose available copies are to be retrieved.
     * @return a list of available copies for the specified book.
     */
    public List<Copy> findAvailableCopies(Book book) {
        TypedQuery<Copy> query = entityManager.createQuery(
                "SELECT c FROM Copy c WHERE c.book = :book AND c.status = 'Available'", Copy.class);
        query.setParameter("book", book);
        return query.getResultList();
    }

    /**
     * Counts all copies associated with a specific book.
     *
     * @param book the {@link Book} whose copies are to be counted.
     * @return the total number of copies for the specified book.
     */
    public Long countAllCopies(Book book) {
        return entityManager.createQuery("SELECT COUNT(c) FROM Copy c WHERE c.book = :book", Long.class)
                .setParameter("book", book)
                .getSingleResult();
    }

    /**
     * Counts all available copies of a specific book.
     *
     * @param book the {@link Book} whose available copies are to be counted.
     * @return the total number of available copies for the specified book.
     */
    public Long countAvailableCopies(Book book) {
        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM Copy c WHERE c.book = :book AND c.status = 'Available'",
                        Long.class)
                .setParameter("book", book)
                .getSingleResult();
    }

    /**
     * Counts all borrowed copies of a specific book.
     *
     * @param book the {@link Book} whose borrowed copies are to be counted.
     * @return the total number of borrowed copies for the specified book.
     */
    public Long countBorrowedCopies(Book book) {
        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM Copy c WHERE c.book = :book AND c.status = 'Borrowed'",
                        Long.class)
                .setParameter("book", book)
                .getSingleResult();
    }
}
