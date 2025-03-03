package table;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a copy of a book in the library system.
 * Each copy belongs to a specific book and can have a status indicating its availability.
 */
@Entity
@Table(name = "copies")
public class Copy {

    /**
     * Unique identifier for the copy, generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Unique number identifying the copy within the library.
     */
    @Column(name = "copy_number", nullable = false, unique = true)
    private Integer copyNumber;

    /**
     * Status of the copy, such as "Available", "Borrowed", or "Withdrawn".
     */
    @Column(nullable = false)
    private String status;

    /**
     * The book to which this copy belongs.
     * Represents a many-to-one relationship with the Book entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * List of borrowings associated with this copy.
     * Represents a one-to-many relationship with the Borrowing entity.
     */
    @OneToMany(mappedBy = "copy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Borrowing> borrowings = new ArrayList<>();

    /**
     * Default constructor required by JPA.
     */
    public Copy() {}

    /**
     * Constructs a copy with the specified book and copy number.
     * The status is initialized to "Available".
     *
     * @param book the book to which this copy belongs.
     * @param copyNumber the unique number identifying this copy.
     */
    public Copy(Book book, Integer copyNumber) {
        this.book = book;
        this.copyNumber = copyNumber;
        this.status = "Available";
    }

    /**
     * Gets the unique identifier for the copy.
     *
     * @return the unique identifier for the copy.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gets the unique number identifying the copy.
     *
     * @return the copy number.
     */
    public Integer getCopyNumber() {
        return copyNumber;
    }

    /**
     * Sets the unique number identifying the copy.
     *
     * @param copyNumber the new copy number.
     */
    public void setCopyNumber(Integer copyNumber) {
        this.copyNumber = copyNumber;
    }

    /**
     * Gets the current status of the copy.
     *
     * @return the current status of the copy.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the copy.
     *
     * @param status the new status ("Available", "Borrowed", "Withdrawn").
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the book to which this copy belongs.
     *
     * @return the associated book.
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the book to which this copy belongs.
     *
     * @param book the associated book.
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Adds a borrowing record to the copy.
     *
     * @param borrowing the borrowing record to add.
     */
    public void addBorrowing(Borrowing borrowing) {
        borrowings.add(borrowing);
        borrowing.setCopy(this);
    }

}
