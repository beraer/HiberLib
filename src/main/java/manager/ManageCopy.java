package manager;

import Repository.CopyRepo;
import jakarta.persistence.EntityManager;
import table.Book;
import table.Copy;

import java.util.List;
import java.util.Optional;

public class ManageCopy {
    private final CopyRepo copyRepo;

    public ManageCopy(EntityManager entityManager) {
        this.copyRepo = new CopyRepo(entityManager);
    }

    /**
     * Creates a new copy of a book
     * @param book The book to create a copy for
     * @param copyNumber The number to assign to this copy
     * @return The created copy
     */
    public Copy createCopy(Book book, Integer copyNumber) {
        Copy copy = new Copy(book, copyNumber);
        return copyRepo.save(copy);
    }

    /**
     * Gets all copies of a specific book
     * @param book The book whose copies to retrieve
     * @return List of copies
     */
    public List<Copy> getCopiesByBook(Book book) {
        return copyRepo.findByBook(book);
    }

    /**
     * Gets available copies of a specific book
     * @param book The book whose available copies to retrieve
     * @return List of available copies
     */
    public List<Copy> getAvailableCopies(Book book) {
        return copyRepo.findAvailableCopies(book);
    }

    /**
     * Gets a copy by its ID
     * @param id The ID of the copy
     * @return Optional containing the copy if found
     */
    public Optional<Copy> getCopyById(Integer id) {
        return copyRepo.findById(id);
    }

    /**
     * Counts available copies of a book
     * @param book The book to count copies for
     * @return Number of available copies
     */
    public Long countAvailableCopies(Book book) {
        return copyRepo.countAvailableCopies(book);
    }


    /**
     * Deletes a copy
     * @param id The ID of the copy to delete
     * @throws Exception if the copy is currently borrowed
     */
    public void deleteCopy(Integer id) throws Exception {
        Optional<Copy> copy = copyRepo.findById(id);
        if (!copy.isPresent()) {
            throw new Exception("Copy not found with id: " + id);
        }

        if ("Borrowed".equals(copy.get().getStatus())) {
            throw new Exception("Cannot delete a borrowed copy");
        }

        copyRepo.deleteById(id);
    }

    /**
     * Updates the status of a copy
     * @param copy The copy to update
     * @param newStatus The new status to set
     * @return The updated copy
     * @throws Exception if the status transition is invalid
     */
    public Copy updateCopyStatus(Copy copy, String newStatus) throws Exception {
        if (!isValidStatusTransition(copy.getStatus(), newStatus)) {
            throw new Exception("Invalid status transition from " + copy.getStatus() + " to " + newStatus);
        }

        copy.setStatus(newStatus);
        return copyRepo.update(copy);
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case "Available":
                return newStatus.equals("Borrowed") || newStatus.equals("Withdrawn");
            case "Borrowed":
                return newStatus.equals("Available");
            case "Withdrawn":
                return newStatus.equals("Available");
            default:
                return false;
        }
    }
    /**
     * Counts total number of copies for a book
     * @param book The book to count copies for
     * @return Total number of copies
     */
    public Long countAllCopies(Book book) {
        return copyRepo.countAllCopies(book);
    }

    /**
     * Counts borrowed copies of a book
     * @param book The book to count borrowed copies for
     * @return Number of borrowed copies
     */
    public Long countBorrowedCopies(Book book) {
        return copyRepo.countBorrowedCopies(book);
    }
}
