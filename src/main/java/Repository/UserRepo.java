package Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import table.User;

import java.util.Optional;

/**
 * Repository class for managing {@link User} entities in the database.
 * Provides methods for performing CRUD operations and custom queries.
 */
public class UserRepo extends AbstractRepo<User> {

    /**
     * Constructs a new {@code UserRepo} instance with the specified {@link EntityManager}.
     *
     * @param entityManager the EntityManager to be used for database operations.
     */
    public UserRepo(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Finds a {@link User} entity by its email.
     *
     * @param email the email of the user to find.
     * @return an {@link Optional} containing the user if found, or {@link Optional#empty()} if not.
     */
    public Optional<User> findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Checks whether a {@link User} entity with the specified email exists in the database.
     *
     * @param email the email to check for existence.
     * @return {@code true} if a user with the specified email exists, {@code false} otherwise.
     */
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
}
