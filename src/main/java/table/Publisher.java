package table;

import jakarta.persistence.*;
import java.util.List;

/**
 * Represents a publisher entity in the library system.
 * This entity is mapped to the "publishers" table in the database.
 */
@Entity
@Table(name = "publishers")
public class Publisher {

    /**
     * Unique identifier for the publisher.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Name of the publisher.
     * This field is mandatory.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Address of the publisher.
     */
    private String address;

    /**
     * Phone number of the publisher.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * List of books associated with the publisher.
     * Represents a one-to-many relationship with the Book entity.
     */
    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books;

    /**
     * Default constructor required by JPA.
     */
    public Publisher() {}

    /**
     * Constructs a publisher with the specified name.
     *
     * @param name the name of the publisher.
     */
    public Publisher(String name) {
        this.name = name;
    }

    /**
     * Gets the publisher's ID.
     *
     * @return the publisher's ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gets the publisher's name.
     *
     * @return the publisher's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the publisher's name.
     *
     * @param name the publisher's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the publisher's address.
     *
     * @param address the publisher's address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Sets the publisher's phone number.
     *
     * @param phoneNumber the publisher's phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
