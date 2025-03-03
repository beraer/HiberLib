package table;

import jakarta.persistence.*;
import validation.ValidEmail;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user entity in the library system.
 * This entity is mapped to the "users" table in the database.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Unique identifier for the user.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Name of the user.
     * This field is mandatory.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Address of the user.
     */
    private String address;

    /**
     * Email of the user, must be unique and valid.
     * This field is mandatory and validated using {@link validation.ValidEmail}.
     */
    @Column(unique = true, nullable = false)
    @ValidEmail
    private String email;

    /**
     * Phone number of the user.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * List of borrowings associated with the user.
     * Represents a one-to-many relationship with the Borrowing entity.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Borrowing> borrowings = new ArrayList<>();

    /**
     * Librarian role associated with the user, if applicable.
     * Represents a one-to-one relationship with the Librarian entity.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Librarian librarian;

    /**
     * Default constructor required by JPA.
     */
    public User() {}

    /**
     * Constructs a user with the specified name and email.
     *
     * @param name  the name of the user.
     * @param email the email of the user.
     */
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Gets the user's ID.
     *
     * @return the user's ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the user's ID.
     *
     * @param id the user's ID.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the user's name.
     *
     * @return the user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name the user's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's email.
     *
     * @return the user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email the user's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's phone number.
     *
     * @return the user's phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phoneNumber the user's phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the user's address.
     *
     * @return the user's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the user's address.
     *
     * @param address the user's address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the borrowings associated with the user.
     *
     * @return the list of borrowings.
     */
    public List<Borrowing> getBorrowings() {
        return borrowings;
    }

    /**
     * Gets the librarian role associated with the user.
     *
     * @return the librarian entity associated with the user, or null if not applicable.
     */
    public Librarian getLibrarian() {
        return librarian;
    }

    /**
     * Sets the librarian role associated with the user.
     *
     * @param librarian the librarian entity to associate with the user.
     */
    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }
}
