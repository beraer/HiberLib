package Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import table.Librarian;
import table.User;

import java.util.Optional;

/**
 * Repository class for managing {@link Librarian} entities in the database.
 * Provides methods for CRUD operations and custom queries related to librarians.
 */
public class LibrarianRepo extends AbstractRepo<Librarian> {

    /**
     * Constructs a new {@code LibrarianRepo} instance with the specified {@link EntityManager}.
     *
     * @param entityManager the EntityManager to be used for database operations.
     */
    public LibrarianRepo(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Checks if the specified {@link User} is a librarian.
     *
     * @param user the user to check.
     * @return {@code true} if the user is a librarian, {@code false} otherwise.
     */
    public boolean isLibrarian(User user) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(l) FROM Librarian l WHERE l.user = :user", Long.class);
        query.setParameter("user", user);
        return query.getSingleResult() > 0;
    }
}
