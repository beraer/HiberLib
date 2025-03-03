package manager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import table.Publisher;
import table.User;

import java.util.Date;

public class TestData {
    private final EntityManager entityManager;
    private final ManageUser userService;
    private final ManageLibrarian manageLibrarian;
    private final ManagePublisher managePublisher;

    /**
     * Constructor for initializing the DatabaseInitializer with necessary services.
     *
     * @param entityManager The EntityManager for database operations.
     */
    public TestData(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.userService = new ManageUser(entityManager);
        this.manageLibrarian = new ManageLibrarian(entityManager);
        this.managePublisher = new ManagePublisher(entityManager);
    }

    /**
     * Initializes the database with test data if the database is empty.
     */
    public void initializeDatabase() {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            if (isDataEmpty()) {
                initializeUsers();
                initializePublishers();
                transaction.commit();
                System.out.println("Database initialized successfully with test data");
            } else {
                transaction.rollback();
                System.out.println("Database already contains data, skipping initialization");
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes publishers with example data.
     *
     * @throws Exception If an error occurs during publisher creation.
     */
    private void initializePublishers() throws Exception {
        createPublisher(
                "Riverdale Books",
                "102 Main Street, Springfield, IL 62701",
                "+1-217-555-1234"
        );

        createPublisher(
                "Haven Press",
                "456 Elm Street, Seattle, WA 98101",
                "+1-206-555-5678"
        );

        createPublisher(
                "Beacon Publishing",
                "789 Maple Avenue, Denver, CO 80201",
                "+1-303-555-9012"
        );

        createPublisher(
                "Pinewood Academic Press",
                "321 Oak Lane, Austin, TX 73301",
                "+1-512-555-3456"
        );

        createPublisher(
                "Northlight Publishers",
                "654 Birch Drive, Boston, MA 02101",
                "+1-617-555-7890"
        );

        createPublisher(
                "Skyline Fiction",
                "987 Cedar Road, San Francisco, CA 94101",
                "+1-415-555-2345"
        );

        createPublisher(
                "Trailblazer Media",
                "159 Redwood Street, Portland, OR 97201",
                "+1-503-555-6789"
        );

        createPublisher(
                "Summit Books",
                "753 Spruce Avenue, Chicago, IL 60601",
                "+1-312-555-0123"
        );
    }

    /**
     * Creates a publisher with the specified name, address, and phone number.
     *
     * @param name         The name of the publisher.
     * @param address      The address of the publisher.
     * @param phoneNumber  The phone number of the publisher.
     * @throws Exception If an error occurs during publisher creation.
     */
    private void createPublisher(String name, String address, String phoneNumber) throws Exception {
        Publisher publisher = new Publisher(name);
        publisher.setAddress(address);
        publisher.setPhoneNumber(phoneNumber);
        managePublisher.createPublisher(publisher);
    }

    /**
     * Initializes users and librarians with example data.
     *
     * @throws Exception If an error occurs during user creation.
     */
    private void initializeUsers() throws Exception {
        User regularUser = new User("Emily Carter", "user@library.com");
        regularUser.setPhoneNumber("223-456-7890");
        regularUser.setAddress("101 User Lane");
        regularUser = userService.registerUser(regularUser);

        User librarianUser = new User("David Baker", "librarian@library.com");
        librarianUser.setPhoneNumber("334-765-4321");
        librarianUser.setAddress("202 Librarian Court");
        librarianUser = userService.registerUser(librarianUser);

        manageLibrarian.createLibrarian(
                librarianUser,
                new Date(),
                "LIBRARIAN"
        );

        User seniorUser = new User("Sophia Wilson", "senior@library.com");
        seniorUser.setPhoneNumber("445-555-5555");
        seniorUser.setAddress("303 Senior Street");
        seniorUser = userService.registerUser(seniorUser);

        manageLibrarian.createLibrarian(
                seniorUser,
                new Date(),
                "SENIOR_LIBRARIAN"
        );
    }

    /**
     * Checks if the database is empty.
     *
     * @return True if the database is empty, otherwise false.
     */
    private boolean isDataEmpty() {
        Long userCount = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();
        return userCount == 0;
    }
}
