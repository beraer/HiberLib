package table;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Represents a librarian entity in the library system.
 * This entity is mapped to the "librarians" table in the database.
 */
@Entity
@Table(name = "librarians")
public class Librarian {

    /**
     * Unique identifier for the librarian.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The user associated with this librarian.
     * Represents a one-to-one relationship with the User entity.
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * The employment date of the librarian.
     */
    @Column(name = "employment_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date employmentDate;

    /**
     * The position of the librarian (e.g., "Librarian" or "Senior Librarian").
     */
    @Column(nullable = false)
    private String position;

    /**
     * Default constructor required by JPA.
     */
    public Librarian() {}

    /**
     * Constructs a librarian with the specified user, employment date, and position.
     *
     * @param user the user associated with this librarian.
     * @param employmentDate the date the librarian was employed.
     * @param position the position of the librarian.
     */
    public Librarian(User user, Date employmentDate, String position) {
        this.user = user;
        this.employmentDate = employmentDate;
        this.position = position;
    }

    /**
     * Gets the librarian's ID.
     *
     * @return the librarian's ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gets the user associated with this librarian.
     *
     * @return the associated user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with this librarian.
     *
     * @param user the user to associate with this librarian.
     */
    public void setUser(User user) {
        this.user = user;
    }

}
