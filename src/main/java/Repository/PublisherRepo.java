package Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import table.Publisher;

import java.util.List;
import java.util.Optional;

/**
 * Repository class for managing {@link Publisher} entities in the database.
 * Provides methods for CRUD operations and custom queries related to publishers.
 */
public class PublisherRepo extends AbstractRepo<Publisher> {

    /**
     * Constructs a new {@code PublisherRepo} instance with the specified {@link EntityManager}.
     *
     * @param entityManager the EntityManager to be used for database operations.
     */
    public PublisherRepo(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Finds a publisher with an exact name match (case-insensitive).
     *
     * @param name the exact name of the publisher to find.
     * @return an {@link Optional} containing the matching publisher, or {@link Optional#empty()} if none found.
     */
    public Optional<Publisher> findByExactName(String name) {
        TypedQuery<Publisher> query = entityManager.createQuery(
                "SELECT p FROM Publisher p WHERE LOWER(p.name) = LOWER(:name)", Publisher.class);
        query.setParameter("name", name);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves all publishers from the database.
     *
     * @return a list of all publishers.
     */
    public List<Publisher> findAll() {
        return entityManager.createQuery("SELECT p FROM Publisher p", Publisher.class).getResultList();
    }
}
