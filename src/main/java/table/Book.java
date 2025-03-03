package table;

import jakarta.persistence.*;
import validation.ValidIsbn;

import java.util.List;

/**
 * Entity representing a book in the library system.
 * Each book is associated with a publisher and can have multiple copies.
 */
@Entity
@Table(name = "books")
public class Book {

    /**
     * Unique identifier for the book, generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Title of the book.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Author of the book.
     */
    private String author;

    /**
     * Year the book was published.
     */
    @Column(name = "publication_year")
    private Integer publicationYear;

    /**
     * ISBN (International Standard Book Number) of the book.
     * This must be unique and adhere to the ISBN validation rules.
     */
    @Column(unique = true, nullable = false)
    @ValidIsbn
    private String isbn;

    /**
     * Publisher associated with the book.
     * Represents a many-to-one relationship with the Publisher entity.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    /**
     * List of copies associated with the book.
     * Represents a one-to-many relationship with the Copy entity.
     */
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Copy> copies;

    /**
     * Default constructor required by JPA.
     */
    public Book() {}

    /**
     * Constructs a book with the specified title and ISBN.
     *
     * @param title the title of the book.
     * @param isbn the ISBN of the book.
     */
    public Book(String title, String isbn) {
        this.title = title;
        this.isbn = isbn;
    }

    /**
     * Gets the unique identifier of the book.
     *
     * @return the unique identifier of the book.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gets the title of the book.
     *
     * @return the title of the book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title the new title of the book.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the author of the book.
     *
     * @return the author of the book.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book.
     *
     * @param author the new author of the book.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the publication year of the book.
     *
     * @return the publication year of the book.
     */
    public Integer getPublicationYear() {
        return publicationYear;
    }

    /**
     * Sets the publication year of the book.
     *
     * @param publicationYear the new publication year of the book.
     */
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    /**
     * Gets the ISBN of the book.
     *
     * @return the ISBN of the book.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param isbn the new ISBN of the book.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the publisher associated with the book.
     *
     * @return the publisher of the book.
     */
    public Publisher getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher associated with the book.
     *
     * @param publisher the new publisher of the book.
     */
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Gets the list of copies associated with the book.
     *
     * @return the list of copies of the book.
     */
    public List<Copy> getCopies() {
        return copies;
    }
}
