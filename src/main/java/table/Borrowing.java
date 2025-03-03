package table;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entity representing a borrowing record in the library system.
 * Each record links a user to a specific copy of a book with borrow and return dates.
 */
@Entity
@Table(name = "borrowings")
public class Borrowing {

    /**
     * Unique identifier for the borrowing record, generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The user associated with this borrowing record.
     * Represents a many-to-one relationship with the User entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The copy of the book being borrowed.
     * Represents a many-to-one relationship with the Copy entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "copy_id", nullable = false)
    private Copy copy;

    /**
     * The date when the borrowing was initiated.
     */
    @Column(name = "borrow_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date borrowDate;

    /**
     * The date when the borrowed copy was returned.
     * Can be null if the copy has not yet been returned.
     */
    @Column(name = "return_date")
    @Temporal(TemporalType.DATE)
    private Date returnDate;

    /**
     * Default constructor required by JPA.
     */
    public Borrowing() {}

    /**
     * Constructs a borrowing record with the specified user, copy, and borrow date.
     *
     * @param user the user borrowing the copy.
     * @param copy the copy being borrowed.
     * @param borrowDate the date of borrowing.
     */
    public Borrowing(User user, Copy copy, Date borrowDate) {
        this.user = user;
        this.copy = copy;
        this.borrowDate = borrowDate;
    }

    /**
     * Gets the unique identifier for the borrowing record.
     *
     * @return the unique identifier for the borrowing record.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the borrowing record.
     *
     * @param id the new identifier for the borrowing record.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the user associated with this borrowing record.
     *
     * @return the user who borrowed the copy.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with this borrowing record.
     *
     * @param user the user who borrowed the copy.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the copy being borrowed.
     *
     * @return the copy being borrowed.
     */
    public Copy getCopy() {
        return copy;
    }

    /**
     * Sets the copy being borrowed.
     *
     * @param copy the copy being borrowed.
     */
    public void setCopy(Copy copy) {
        this.copy = copy;
    }

    /**
     * Gets the date when the borrowing was initiated.
     *
     * @return the borrow date.
     */
    public Date getBorrowDate() {
        return borrowDate;
    }

    /**
     * Gets the date when the borrowed copy was returned.
     *
     * @return the return date, or null if not returned.
     */
    public Date getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the date when the borrowed copy was returned.
     *
     * @param returnDate the return date.
     */
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}
