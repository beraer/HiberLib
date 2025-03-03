package manager;

import Repository.LibrarianRepo;
import jakarta.persistence.EntityManager;
import table.Librarian;
import table.User;

import java.util.Date;

public class ManageLibrarian {
    private final LibrarianRepo librarianRepo;

    public ManageLibrarian(EntityManager entityManager) {
        this.librarianRepo = new LibrarianRepo(entityManager);
    }

    /**
     * Creates a new librarian
     * @param user The user to make a librarian
     * @param employmentDate The date of employment
     * @param position The position of the librarian
     * @return The created librarian
     * @throws Exception if user is already a librarian
     */
    public Librarian createLibrarian(User user, Date employmentDate, String position) throws Exception {
        if (librarianRepo.isLibrarian(user)) {
            throw new Exception("User is already a librarian: " + user.getEmail());
        }

        Librarian librarian = new Librarian(user, employmentDate, position);
        return librarianRepo.save(librarian);
    }


    /**
     * Checks if a user is a librarian
     * @param user The user to check
     * @return true if the user is a librarian
     */
    public boolean isLibrarian(User user) {
        return librarianRepo.isLibrarian(user);
    }
}