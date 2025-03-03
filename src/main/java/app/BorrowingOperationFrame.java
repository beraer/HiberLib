package app;

import manager.ManageBook;
import manager.ManageBorrowing;
import manager.ManageCopy;
import manager.ManageUser;
import table.Book;
import table.Borrowing;
import table.Copy;
import table.User;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class BorrowingOperationFrame extends JDialog {
    private final ManageUser userService;
    private final ManageBook manageBook;
    private final ManageCopy manageCopy;
    private final ManageBorrowing manageBorrowing;
    private final User librarian;
    private boolean borrowingCreated = false;

    private JComboBox<User> userComboBox;
    private JComboBox<Book> bookComboBox;
    private JComboBox<Copy> copyComboBox;
    private JLabel availableCopiesLabel;
    private JButton createButton;
    private JButton cancelButton;

    public BorrowingOperationFrame(JFrame parent, ManageUser userService, ManageBook manageBook,
                                   ManageCopy manageCopy, ManageBorrowing manageBorrowing, User librarian) {
        super(parent, "New Borrowing", true);
        this.userService = userService;
        this.manageBook = manageBook;
        this.manageCopy = manageCopy;
        this.manageBorrowing = manageBorrowing;
        this.librarian = librarian;

        setSize(500, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        loadUsers();
        loadBooks();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // User selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("User:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        userComboBox = new JComboBox<>();
        userComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User) {
                    User user = (User) value;
                    setText(user.getName() + " (" + user.getEmail() + ")");
                }
                return this;
            }
        });
        mainPanel.add(userComboBox, gbc);

        // Book selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Book:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        bookComboBox = new JComboBox<>();
        bookComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Book) {
                    Book book = (Book) value;
                    String author = book.getAuthor() != null ? " by " + book.getAuthor() : "";
                    setText(book.getTitle() + author + " (ISBN: " + book.getIsbn() + ")");
                }
                return this;
            }
        });
        bookComboBox.addActionListener(e -> updateAvailableCopies());
        mainPanel.add(bookComboBox, gbc);

        // Available copies label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Available Copies:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        availableCopiesLabel = new JLabel("Select a book to see available copies");
        availableCopiesLabel.setForeground(Color.BLUE);
        mainPanel.add(availableCopiesLabel, gbc);

        // Copy selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Copy:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        copyComboBox = new JComboBox<>();
        copyComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Copy) {
                    Copy copy = (Copy) value;
                    setText(String.format("Copy #%d (Status: %s)",
                            copy.getCopyNumber(),
                            copy.getStatus()));
                }
                return this;
            }
        });
        mainPanel.add(copyComboBox, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        createButton = new JButton("Create");
        cancelButton = new JButton("Cancel");

        createButton.addActionListener(e -> createBorrowing());
        cancelButton.addActionListener(e -> dispose());

        buttonsPanel.add(createButton);
        buttonsPanel.add(cancelButton);

        // Add panels to dialog
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        List<User> users = userService.retrieveAllUsers();
        userComboBox.removeAllItems();
        for (User user : users) {
            userComboBox.addItem(user);
        }
    }

    private void loadBooks() {
        List<Book> books = manageBook.getAllBooks();
        bookComboBox.removeAllItems();
        for (Book book : books) {
            bookComboBox.addItem(book);
        }
        if (bookComboBox.getItemCount() > 0) {
            updateAvailableCopies();
        }
    }

    private void updateAvailableCopies() {
        copyComboBox.removeAllItems();
        Book selectedBook = (Book) bookComboBox.getSelectedItem();

        if (selectedBook != null) {
            try {
                Long totalCopies = manageCopy.countAllCopies(selectedBook);
                Long availableCopies = manageCopy.countAvailableCopies(selectedBook);
                Long borrowedCopies = manageCopy.countBorrowedCopies(selectedBook);

                availableCopiesLabel.setText(String.format("%d available out of %d total copies (%d currently borrowed)",
                        availableCopies, totalCopies, borrowedCopies));

                List<Copy> copies = manageCopy.getAvailableCopies(selectedBook);
                if (copies.isEmpty()) {
                    availableCopiesLabel.setForeground(Color.RED);
                    createButton.setEnabled(false);
                } else {
                    availableCopiesLabel.setForeground(Color.BLUE);
                    createButton.setEnabled(true);
                    for (Copy copy : copies) {
                        copyComboBox.addItem(copy);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error loading copies: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            availableCopiesLabel.setText("No book selected");
            availableCopiesLabel.setForeground(Color.RED);
            createButton.setEnabled(false);
        }
    }

    private void createBorrowing() {
        User selectedUser = (User) userComboBox.getSelectedItem();
        Copy selectedCopy = (Copy) copyComboBox.getSelectedItem();

        if (selectedUser == null || selectedCopy == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select both a user and a copy.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Borrowing borrowing = manageBorrowing.newBorrowing(selectedUser, selectedCopy, new Date());
            borrowingCreated = true;
            JOptionPane.showMessageDialog(this,
                    String.format("Borrowing created successfully%nID: %d%nUser: %s%nBook: %s%nCopy: #%d",
                            borrowing.getId(),
                            selectedUser.getName(),
                            selectedCopy.getBook().getTitle(),
                            selectedCopy.getCopyNumber()),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error creating borrowing: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isBorrowingCreated() {
        return borrowingCreated;
    }
}