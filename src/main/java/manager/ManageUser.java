package manager;

import Repository.UserRepo;
import jakarta.persistence.EntityManager;
import table.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing user-related operations.
 */
public class ManageUser {

    private final UserRepo userRepo;
    private final ManageLibrarian manageLibrarian;

    /**
     * Constructor for injecting dependencies.
     *
     * @param entityManager The EntityManager for database operations.
     */
    public ManageUser(EntityManager entityManager) {
        this.userRepo = new UserRepo(entityManager);
        this.manageLibrarian = new ManageLibrarian(entityManager);
    }

    /**
     * Registers a new user.
     *
     * @param user The user to create.
     * @return The created user.
     * @throws Exception If the user's email already exists.
     */
    public User registerUser(User user) throws Exception {
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new Exception("Email already exists: " + user.getEmail());
        }
        return userRepo.save(user);
    }

    /**
     * Updates an existing user's information.
     *
     * @param user The user to update.
     * @return The updated user.
     * @throws Exception If the user is not found or the email is already in use by another user.
     */
    public User updateUserDetails(User user) throws Exception {
        if (userRepo.findById(user.getId()).isEmpty()) {
            throw new Exception("User not found with id: " + user.getId());
        }

        Optional<User> existingUser = userRepo.findByEmail(user.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            throw new Exception("Email already exists: " + user.getEmail());
        }

        return userRepo.update(user);
    }

    /**
     * Removes a user by their ID.
     *
     * @param userId The ID of the user to delete.
     * @throws Exception If the user does not exist or has active borrowings.
     */
    public void removeUserById(Integer userId) throws Exception {
        Optional<User> user = userRepo.findById(userId);
        if (user.isEmpty()) {
            throw new Exception("User not found with id: " + userId);
        }

        if (!user.get().getBorrowings().isEmpty()) {
            throw new Exception("Cannot delete user with active borrowings.");
        }

        userRepo.deleteById(userId);
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId The ID of the user.
     * @return An Optional containing the user if found.
     */
    public Optional<User> findUserById(Integer userId) {
        return userRepo.findById(userId);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user.
     * @return An Optional containing the user if found.
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    /**
     * Retrieves all users.
     *
     * @return A list of all users.
     */
    public List<User> retrieveAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Promotes a user to the librarian role.
     *
     * @param userId   The ID of the user to promote.
     * @param isSenior Whether the user should be promoted to a senior librarian.
     * @throws Exception If the user does not exist or is already a librarian.
     */
    public void promoteUserToLibrarian(Integer userId, boolean isSenior) throws Exception {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found with id: " + userId);
        }

        User user = userOpt.get();
        if (manageLibrarian.isLibrarian(user)) {
            throw new Exception("User is already a librarian.");
        }

        String role = isSenior
                ? LibraryConfigLoader.getSeniorLibrarianRole()
                : LibraryConfigLoader.getLibrarianRole();

        manageLibrarian.createLibrarian(user, new Date(), role);
    }
}
