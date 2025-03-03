package app;

import jakarta.persistence.EntityManager;
import manager.*;
import table.Book;
import table.Borrowing;
import table.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class LibrarianFrame extends JFrame {
    private final User librarian;
    private final ManageBook manageBook;
    private final ManageUser userService;
    private final ManageBorrowing manageBorrowing;
    private final ManageCopy manageCopy;
    private final ManagePublisher managePublisher;
    private final ManageLibrarian manageLibrarian;

    private JTabbedPane tabbedPane;
    private JTable booksTable;
    private JTable usersTable;
    private JTable borrowingsTable;
    private DefaultTableModel booksTableModel;
    private DefaultTableModel usersTableModel;
    private DefaultTableModel borrowingsTableModel;
    private JCheckBox showReturnedCheckBox;
    private JCheckBox showOverdueCheckBox;

    public LibrarianFrame(User librarian, EntityManager entityManager) {
        this.librarian = librarian;
        this.manageBook = new ManageBook(entityManager);
        this.userService = new ManageUser(entityManager);
        this.manageBorrowing = new ManageBorrowing(entityManager);
        this.manageCopy = new ManageCopy(entityManager);
        this.managePublisher = new ManagePublisher(entityManager);
        this.manageLibrarian = new ManageLibrarian(entityManager);

        setTitle("Library Management System - Librarian: " + librarian.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        initComponents();
        loadData();
    }

    private void initComponents() {
        // Create a header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(51, 122, 183));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel headerLabel = new JLabel("Welcome, Librarian " + librarian.getName());
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.WEST);

        // Initialize tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);

        // Add tabs
        tabbedPane.addTab("Books Management", createBooksPanel());
        tabbedPane.addTab("Users Management", createUsersPanel());
        tabbedPane.addTab("Borrowings Management", createBorrowingsPanel());

        // Main layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // Create menu bar
        createMenuBar();
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);

        // Basic table settings
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.BLACK); // Ensure text is black
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(207, 226, 243));
        table.setSelectionForeground(Color.BLACK); // Ensure text is black when selected
        table.getTableHeader().setReorderingAllowed(false);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(51, 122, 183));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        // Custom renderer for table cells
        javax.swing.table.DefaultTableCellRenderer cellRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Ensure the text color is always black
                c.setForeground(Color.BLACK);

                // Set the background for selected and unselected rows
                if (isSelected) {
                    c.setBackground(new Color(207, 226, 243)); // Light blue for selection
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250)); // Alternate row colors
                }

                // Add padding to the cell
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return c;
            }
        };

        // Apply the renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        return table;
    }


    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(51, 122, 183));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(40, 96, 144));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 122, 183));
            }
        });

        return button;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 122, 183)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);

        JTextField searchField = createStyledTextField();
        JButton searchButton = createStyledButton("Search Books");

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        searchButton.addActionListener(e -> searchBooks(searchField.getText()));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createStyledButton("Add Book");
        JButton editButton = createStyledButton("Edit Book");
        JButton deleteButton = createStyledButton("Delete Book");
        JButton manageCopiesButton = createStyledButton("Manage Copies");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(manageCopiesButton);

        // Action listeners
        addButton.addActionListener(e -> showAddBookDialog());
        editButton.addActionListener(e -> showEditBookDialog());
        deleteButton.addActionListener(e -> deleteSelectedBook());
        manageCopiesButton.addActionListener(e -> showManageCopiesDialog());

        // Books Table
        String[] bookColumns = {
                "ID", "Title", "Author", "ISBN", "Publication Year",
                "Publisher", "Total Copies", "Available Copies"
        };

        booksTableModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        booksTable = createStyledTable(booksTableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        // Control Panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(searchPanel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create table model for users
        String[] userColumns = {
                "ID", "Name", "Email", "Phone", "Address", "Role", "Active Borrowings"
        };
        usersTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable = createStyledTable(usersTableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add scroll pane for table
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton makeLibrarianButton = new JButton("Make Librarian");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(makeLibrarianButton);

        // Add action listeners
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        makeLibrarianButton.addActionListener(e -> makeSelectedUserLibrarian());

        panel.add(buttonPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createBorrowingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create table model for borrowings
        String[] borrowingColumns = {
                "ID", "User", "Book Title", "Borrow Date", "Due Date",
                "Return Date", "Status", "Late Fee"
        };
        borrowingsTableModel = new DefaultTableModel(borrowingColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Remove the special handling for the late fee column
                return String.class;
            }
        };
        borrowingsTable = createStyledTable(borrowingsTableModel);
        borrowingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add scroll pane for table
        JScrollPane scrollPane = new JScrollPane(borrowingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create top control panel
        JPanel topControlPanel = new JPanel(new BorderLayout());

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton newBorrowingButton = new JButton("New Borrowing");
        JButton returnBookButton = new JButton("Return Book");
        JButton renewButton = new JButton("Renew Borrowing");

        buttonPanel.add(newBorrowingButton);
        buttonPanel.add(returnBookButton);
        buttonPanel.add(renewButton);


        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        showReturnedCheckBox = new JCheckBox("Show Returned");
        showOverdueCheckBox = new JCheckBox("Show Overdue Only");
        filterPanel.add(showReturnedCheckBox);
        filterPanel.add(showOverdueCheckBox);

        // Add panels to top control panel
        topControlPanel.add(buttonPanel, BorderLayout.WEST);
        topControlPanel.add(filterPanel, BorderLayout.EAST);

        // Add action listeners
        newBorrowingButton.addActionListener(e -> showNewBorrowingDialog());
        returnBookButton.addActionListener(e -> showReturnBookDialog());
        renewButton.addActionListener(e -> renewSelectedBorrowing());

        // Add filter listeners
        showReturnedCheckBox.addActionListener(e -> loadBorrowings());
        showOverdueCheckBox.addActionListener(e -> loadBorrowings());

        panel.add(topControlPanel, BorderLayout.NORTH);

        return panel;
    }



    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.GRAY);
        menuBar.setBorder(null);

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        fileMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JMenuItem refreshItem = new JMenuItem("Refresh Data");
        JMenuItem exitItem = new JMenuItem("Exit");

        styleMenuItem(refreshItem);
        styleMenuItem(exitItem);

        refreshItem.addActionListener(e -> loadData());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Reports Menu
        JMenu reportsMenu = new JMenu("Reports");
        reportsMenu.setForeground(Color.WHITE);
        reportsMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        menuBar.add(fileMenu);
        menuBar.add(reportsMenu);

        setJMenuBar(menuBar);
    }

    private void styleMenuItem(JMenuItem item) {
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setBackground(Color.WHITE);
        item.setForeground(Color.BLACK);
    }

    private void loadData() {
        loadBooks();
        loadUsers();
        loadBorrowings();
    }

    private void loadBooks() {
        booksTableModel.setRowCount(0);
        List<Book> books = manageBook.getAllBooks();
        for (Book book : books) {
            Object[] row = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getPublicationYear(),
                    book.getPublisher() != null ? book.getPublisher().getName() : "",
                    book.getCopies() != null ? book.getCopies().size() : 0,
                    manageCopy.countAvailableCopies(book)
            };
            booksTableModel.addRow(row);
        }
    }

    private void loadUsers() {
        usersTableModel.setRowCount(0);
        List<User> users = userService.retrieveAllUsers();
        for (User user : users) {
            Object[] row = {
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getAddress(),
                    manageLibrarian.isLibrarian(user) ? "Librarian" : "User",
                    user.getBorrowings() != null ? user.getBorrowings().size() : 0
            };
            usersTableModel.addRow(row);
        }
    }

    private void loadBorrowings() {
        borrowingsTableModel.setRowCount(0);
        List<Borrowing> borrowings;

        if (showOverdueCheckBox.isSelected()) {
            // Add logic for overdue borrowings if needed
            borrowings = manageBorrowing.getCurrentBorrowings(); // Placeholder
        } else if (!showReturnedCheckBox.isSelected()) {
            borrowings = manageBorrowing.getCurrentBorrowings();
        } else {
            borrowings = manageBorrowing.getUserBorrowingHistory(null); // Ensure this handles null correctly
        }

        for (Borrowing borrowing : borrowings) {
            borrowingsTableModel.addRow(new Object[]{
                    borrowing.getId(),
                    borrowing.getUser().getName(),
                    borrowing.getCopy().getBook().getTitle(),
                    formatDate(borrowing.getBorrowDate()),
                    formatDate(null), // Replace with actual due date if needed
                    formatDate(borrowing.getReturnDate()),
                    borrowing.getReturnDate() == null ? "Active" : "Returned",
                    "-" // Replace with actual fee logic if needed
            });
        }
    }


    private String formatDate(Date date) {
        if (date == null) return "-";
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    // Dialog methods
    private void showAddBookDialog() {
        BookOperationFrame dialog = new BookOperationFrame(this, null, manageBook, managePublisher);
        dialog.setVisible(true);
        if (dialog.isBookSaved()) {
            loadBooks();
        }
    }

    private void showEditBookDialog() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer bookId = (Integer) booksTable.getValueAt(selectedRow, 0);
        manageBook.getBookById(bookId).ifPresent(book -> {
            BookOperationFrame dialog = new BookOperationFrame(this, book, manageBook, managePublisher);
            dialog.setVisible(true);
            if (dialog.isBookSaved()) {
                loadBooks();
            }
        });
    }

    private void deleteSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer bookId = (Integer) booksTable.getValueAt(selectedRow, 0);
        try {
            manageBook.deleteBook(bookId);
            loadBooks();
            JOptionPane.showMessageDialog(this, "Book deleted successfully", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage(), "Deletion Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper methods
    private Date calculateDueDate(Date borrowDate) {
        // Implement actual calculation, e.g., 14 days from borrow date
        long millisInDay = 24 * 60 * 60 * 1000;
        return new Date(borrowDate.getTime() + (14 * millisInDay));
    }

    private String formatLateFee(double fee) {
        return fee > 0 ? String.format("$%.2f", fee) : "-";
    }

    private void searchBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadBooks();
            return;
        }
        booksTableModel.setRowCount(0);
        List<Book> books = manageBook.searchBooksByTitle(searchTerm);
        for (Book book : books) {
            Object[] row = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getPublicationYear(),
                    book.getPublisher() != null ? book.getPublisher().getName() : "",
                    book.getCopies() != null ? book.getCopies().size() : 0,
                    manageCopy.countAvailableCopies(book)
            };
            booksTableModel.addRow(row);
        }
    }


    private void showLibraryStats() {
        List<Book> allBooks = manageBook.getAllBooks();
        List<User> allUsers = userService.retrieveAllUsers();
        List<Borrowing> activeBorrowings = manageBorrowing.getCurrentBorrowings();

        StringBuilder stats = new StringBuilder("Library Statistics\n\n");
        stats.append(String.format("Total Books: %d\n", allBooks.size()));
        stats.append(String.format("Total Users: %d\n", allUsers.size()));
        stats.append(String.format("Active Borrowings: %d\n", activeBorrowings.size()));

        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);

        JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                "Library Statistics", JOptionPane.INFORMATION_MESSAGE);
    }



    /**
     * Opens the Manage Copies dialog for the selected book.
     */
    private void showManageCopiesDialog() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to manage its copies", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer bookId = (Integer) booksTable.getValueAt(selectedRow, 0);
        manageBook.getBookById(bookId).ifPresent(book -> {
            CopyOperationFrame dialog = new CopyOperationFrame(this, book, manageCopy);
            dialog.setVisible(true);
            if (dialog.isCopyUpdated()) {
                loadBooks();
            }
        });
    }

    private void showAddUserDialog() {
        UserOperationFrame dialog = new UserOperationFrame(this, null, userService);
        dialog.setVisible(true);
        if (dialog.isUserSaved()) {
            loadUsers();
        }
    }

    private void showEditUserDialog() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer userId = (Integer) usersTable.getValueAt(selectedRow, 0);
        userService.findUserById(userId).ifPresent(user -> {
            UserOperationFrame dialog = new UserOperationFrame(this, user, userService);
            dialog.setVisible(true);
            if (dialog.isUserSaved()) {
                loadUsers();
            }
        });
    }

    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer userId = (Integer) usersTable.getValueAt(selectedRow, 0);
        try {
            userService.removeUserById(userId);
            loadUsers();
            JOptionPane.showMessageDialog(this, "User deleted successfully", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(), "Deletion Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void makeSelectedUserLibrarian() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to promote",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer userId = (Integer) usersTable.getValueAt(selectedRow, 0);
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Make this user a senior librarian?",
                "Librarian Type",
                JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (choice != JOptionPane.CANCEL_OPTION) {
            try {
                userService.promoteUserToLibrarian(userId, choice == JOptionPane.YES_OPTION);
                loadUsers();
                JOptionPane.showMessageDialog(this,
                        "User promoted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error promoting user: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showReturnBookDialog() {
        int selectedRow = borrowingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a borrowing to return", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer borrowingId = (Integer) borrowingsTable.getValueAt(selectedRow, 0);
        manageBorrowing.getBorrowingById(borrowingId).ifPresent(borrowing -> {
            try {
                manageBorrowing.returnBook(borrowingId, new Date());
                JOptionPane.showMessageDialog(this, "Book returned successfully", "Return Successful", JOptionPane.INFORMATION_MESSAGE);
                loadBorrowings();
                loadBooks();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage(), "Return Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void renewSelectedBorrowing() {
        int selectedRow = borrowingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a borrowing to renew", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer borrowingId = (Integer) borrowingsTable.getValueAt(selectedRow, 0);
        manageBorrowing.getBorrowingById(borrowingId).ifPresent(borrowing -> {
            try {
                manageBorrowing.renewBorrowing(borrowingId);
                JOptionPane.showMessageDialog(this, "Borrowing renewed successfully", "Renewal Successful", JOptionPane.INFORMATION_MESSAGE);
                loadBorrowings();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error renewing borrowing: " + e.getMessage(), "Renewal Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void showNewBorrowingDialog() {
        BorrowingOperationFrame dialog = new BorrowingOperationFrame(this, userService, manageBook, manageCopy, manageBorrowing, librarian);
        dialog.setVisible(true);
        if (dialog.isBorrowingCreated()) {
            loadBorrowings();
            loadBooks();
        }
    }
}
