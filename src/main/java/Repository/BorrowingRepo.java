package Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import table.Borrowing;
import table.User;

import java.util.List;

/**
 * Repository class for managing {@link Borrowing} entities.
 * Provides methods for performing database operations related to borrowings.
 */
public class BorrowingRepo extends AbstractRepo<Borrowing> {

    /**
     * Constructs a {@code BorrowingRepo} with the specified {@link EntityManager}.
     *
     * @param entityManager the {@link EntityManager} to be used for database operations.
     */
    public BorrowingRepo(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Retrieves all borrowings associated with a specific user.
     *
     * @param user the {@link User} whose borrowings are to be retrieved.
     * @return a list of borrowings for the specified user.
     */
    public List<Borrowing> findByUser(User user) {
        TypedQuery<Borrowing> query = entityManager.createQuery(
                "SELECT b FROM Borrowing b WHERE b.user = :user", Borrowing.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    /**
     * Retrieves all current borrowings that have not been returned yet.
     *
     * @return a list of current borrowings with no return date.
     */
    public List<Borrowing> findCurrentBorrowings() {
        TypedQuery<Borrowing> query = entityManager.createQuery(
                "SELECT b FROM Borrowing b WHERE b.returnDate IS NULL", Borrowing.class);
        return query.getResultList();
    }

    /**
     * Retrieves the borrowing history of a specific user, ordered by the borrow date in descending order.
     *
     * @param user the {@link User} whose borrowing history is to be retrieved.
     * @return a list of borrowings for the specified user, ordered by borrow date.
     */
    public List<Borrowing> findUserBorrowingHistory(User user) {
        return entityManager.createQuery(
                        "SELECT b FROM Borrowing b WHERE b.user = :user ORDER BY b.borrowDate DESC",
                        Borrowing.class)
                .setParameter("user", user)
                .getResultList();
    }

    /**
     * Retrieves the current active borrowings of a specific user.
     *
     * @param user the {@link User} whose active borrowings are to be retrieved.
     * @return a list of active borrowings for the specified user with no return date.
     */
    public List<Borrowing> findUserCurrentBorrowings(User user) {
        return entityManager.createQuery(
                        "SELECT b FROM Borrowing b WHERE b.user = :user AND b.returnDate IS NULL",
                        Borrowing.class)
                .setParameter("user", user)
                .getResultList();
    }
}
