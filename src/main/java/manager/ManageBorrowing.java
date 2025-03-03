package manager;

import Repository.BorrowingRepo;
import Repository.CopyRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import table.Borrowing;
import table.Copy;
import table.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ManageBorrowing {
    private final BorrowingRepo borrowingRepo;
    private final CopyRepo copyRepo;
    private final EntityManager entityManager;

    public ManageBorrowing(EntityManager entityManager) {
        this.borrowingRepo = new BorrowingRepo(entityManager);
        this.entityManager = entityManager;
        this.copyRepo = new CopyRepo(entityManager);
    }

    public Borrowing newBorrowing(User user, Copy copy, Date borrowDate) throws Exception {
        return ConCheck.lockBorrowing(() -> {
            entityManager.getTransaction().begin();
            try {
                User managedUser = entityManager.contains(user) ?
                        user : entityManager.find(User.class, user.getId());
                Copy managedCopy = entityManager.find(Copy.class, copy.getId(),
                        LockModeType.PESSIMISTIC_WRITE);  // Add pessimistic lock

                if (managedCopy == null || managedUser == null) {
                    throw new Exception("Copy or User not found");
                }
                entityManager.refresh(managedCopy);

                if ("Borrowed".equals(managedCopy.getStatus())) {
                    throw new Exception("Copy is already borrowed");
                }

                long activeBorrowings = borrowingRepo.findUserCurrentBorrowings(managedUser).size();
                if (activeBorrowings >= LibraryConfigLoader.getMaxBorrowingsPerUser()) {
                    throw new Exception("User has reached maximum allowed borrowings: " +
                            LibraryConfigLoader.getMaxBorrowingsPerUser());
                }

                managedCopy.setStatus("Borrowed");

                Borrowing borrowing = new Borrowing(managedUser, managedCopy, borrowDate);
                borrowingRepo.save(borrowing);

                managedCopy.addBorrowing(borrowing);
                copyRepo.update(managedCopy);

                entityManager.getTransaction().commit();
                return borrowing;

            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new Exception("Error creating borrowing: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Returns a borrowed book and calculates any late fees
     */
    public Borrowing returnBook(Integer borrowingId, Date returnDate) throws Exception {
        entityManager.getTransaction().begin();
        try {
            Optional<Borrowing> borrowingOpt = borrowingRepo.findById(borrowingId);
            if (!borrowingOpt.isPresent()) {
                throw new Exception("Borrowing record not found with id: " + borrowingId);
            }

            Borrowing borrowing = borrowingOpt.get();
            entityManager.lock(borrowing, LockModeType.PESSIMISTIC_WRITE);

            if (borrowing.getReturnDate() != null) {
                throw new Exception("Book has already been returned");
            }
            borrowing.setReturnDate(returnDate);

            Copy copy = borrowing.getCopy();
            copy.setStatus("Available");
            copyRepo.update(copy);

            borrowingRepo.update(borrowing);

            entityManager.getTransaction().commit();
            return borrowing;

        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new Exception("Error returning book: " + e.getMessage(), e);
        }
    }

    /**
     * Renews a borrowing if allowed
     */
    public Borrowing renewBorrowing(Integer borrowingId) throws Exception {
        entityManager.getTransaction().begin();
        try {
            Optional<Borrowing> borrowingOpt = borrowingRepo.findById(borrowingId);
            if (!borrowingOpt.isPresent()) {
                throw new Exception("Borrowing record not found with id: " + borrowingId);
            }

            Borrowing borrowing = borrowingOpt.get();

            entityManager.refresh(borrowing);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(borrowing.getBorrowDate());
            calendar.add(Calendar.DAY_OF_MONTH, LibraryConfigLoader.getStandardLoanDays());

            Borrowing updatedBorrowing = borrowingRepo.update(borrowing);
            entityManager.getTransaction().commit();
            return updatedBorrowing;

        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new Exception("Error renewing borrowing: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all current (unreturned) borrowings
     */
    public List<Borrowing> getCurrentBorrowings() {
        return borrowingRepo.findCurrentBorrowings();
    }


    /**
     * Gets a specific borrowing by ID
     */
    public Optional<Borrowing> getBorrowingById(Integer id) {
        return borrowingRepo.findById(id);
    }

    /**
     * Gets complete borrowing history for a user, ordered by borrow date
     * @param user The user whose history to retrieve
     * @return List of all borrowings for the user
     */
    public List<Borrowing> getUserBorrowingHistory(User user) {
        return borrowingRepo.findUserBorrowingHistory(user);
    }

}