package manager;

import Repository.BookRepo;
import Repository.CopyRepo;
import jakarta.persistence.EntityManager;
import table.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ManageBook {
    private final BookRepo bookRepo;
    private final CopyRepo copyRepo;
    private final EntityManager entityManager;


    public ManageBook(EntityManager entityManager) {
        this.bookRepo = new BookRepo(entityManager);
        this.copyRepo = new CopyRepo(entityManager);
        this.entityManager = entityManager;
    }

    /**
     * Creates a new book in the system
     * @param book The book entity to create
     * @return The created book with generated ID
     * @throws Exception if book with the same ISBN already exists
     */
    public Book createBook(Book book) throws Exception {
        Optional<Book> existingBook = bookRepo.findByIsbn(book.getIsbn());
        if (existingBook.isPresent()) {
            throw new Exception("Book with ISBN already exists: " + book.getIsbn());
        }

        return bookRepo.save(book);
    }

    /**
     * Retrieves a book by its ID
     * @param id The ID of the book to retrieve
     * @return Optional containing the book if found
     */
    public Optional<Book> getBookById(Integer id) {
        return bookRepo.findById(id);
    }


    /**
     * Searches for books by title (partial match)
     * @param title The title to search for
     * @return List of books matching the title
     */
    public List<Book> searchBooksByTitle(String title) {
        return bookRepo.findByTitle(title);
    }

    /**
     * Retrieves all books in the system
     * @return List of all books
     */
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }
    

    /**
     * Updates an existing book
     * @param book The book entity to update
     * @return The updated book
     * @throws Exception if book doesn't exist or if new ISBN conflicts with existing book
     */
    public Book updateBook(Book book) throws Exception {
        if (!bookRepo.findById(book.getId()).isPresent()) {
            throw new Exception("Book not found with id: " + book.getId());
        }

        // Check if new ISBN conflicts with another book
        Optional<Book> existingBook = bookRepo.findByIsbn(book.getIsbn());
        if (existingBook.isPresent() && !existingBook.get().getId().equals(book.getId())) {
            throw new Exception("Another book with this ISBN already exists: " + book.getIsbn());
        }

        return bookRepo.update(book);
    }

    /**
     * Deletes a book by its ID
     * @param id The ID of the book to delete
     * @throws Exception if book doesn't exist or has copies
     */
    public void deleteBook(Integer id) throws Exception {
        Optional<Book> book = bookRepo.findById(id);
        if (!book.isPresent()) {
            throw new Exception("Book not found with id: " + id);
        }

        // Check if book has any copies before deletion
        if (book.get().getCopies() != null && !book.get().getCopies().isEmpty()) {
            throw new Exception("Cannot delete book that has copies");
        }

        bookRepo.deleteById(id);
    }

    /**
     * Gets detailed availability information for a book
     * @param book The book to check
     * @return Map containing total copies, available copies, and borrowed copies
     */
    public Map<String, Long> getBookAvailabilityInfo(Book book) {
        Map<String, Long> availabilityInfo = new HashMap<>();
        availabilityInfo.put("totalCopies", copyRepo.countAllCopies(book));
        availabilityInfo.put("availableCopies", copyRepo.countAvailableCopies(book));
        availabilityInfo.put("borrowedCopies", copyRepo.countBorrowedCopies(book));
        return availabilityInfo;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

}