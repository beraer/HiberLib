package Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

/**
 * Abstract repository class for managing generic CRUD operations on entities.
 *
 * @param <T> the type of the entity managed by this repository.
 */
public abstract class AbstractRepo<T> {
    protected final EntityManager entityManager;
    protected final Class<T> entityClass;

    /**
     * Constructs a new {@code AbstractRepo} instance with the specified {@link EntityManager}.
     *
     * @param entityManager the EntityManager to be used for database operations.
     */
    @SuppressWarnings("unchecked")
    public AbstractRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Saves a new entity in the database.
     *
     * @param entity the entity to save.
     * @return the saved entity.
     */
    public T save(T entity) {
        boolean isActive = entityManager.getTransaction().isActive();
        if (!isActive) {
            entityManager.getTransaction().begin();
        }
        try {
            entityManager.persist(entity);
            if (!isActive) {
                entityManager.getTransaction().commit();
            }
            return entity;
        } catch (Exception e) {
            if (!isActive && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity to update.
     * @return the updated entity.
     */
    public T update(T entity) {
        boolean isActive = entityManager.getTransaction().isActive();
        if (!isActive) {
            entityManager.getTransaction().begin();
        }
        try {
            T merged = entityManager.merge(entity);
            if (!isActive) {
                entityManager.getTransaction().commit();
            }
            return merged;
        } catch (Exception e) {
            if (!isActive && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Finds an entity by its ID.
     *
     * @param id the ID of the entity.
     * @return an {@link Optional} containing the entity if found, or empty otherwise.
     */
    public Optional<T> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    /**
     * Retrieves all entities of the type managed by this repository.
     *
     * @return a list of all entities.
     */
    public List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rootEntry = cq.from(entityClass);
        CriteriaQuery<T> all = cq.select(rootEntry);
        TypedQuery<T> allQuery = entityManager.createQuery(all);
        return allQuery.getResultList();
    }

    /**
     * Deletes an entity from the database.
     *
     * @param entity the entity to delete.
     */
    public void delete(T entity) {
        boolean isActive = entityManager.getTransaction().isActive();
        if (!isActive) {
            entityManager.getTransaction().begin();
        }
        try {
            entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            if (!isActive) {
                entityManager.getTransaction().commit();
            }
        } catch (Exception e) {
            if (!isActive && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Deletes an entity by its ID.
     *
     * @param id the ID of the entity to delete.
     * @return {@code true} if the entity was deleted, {@code false} otherwise.
     */
    public boolean deleteById(Integer id) {
        Optional<T> entity = findById(id);
        if (entity.isPresent()) {
            delete(entity.get());
            return true;
        }
        return false;
    }
}
