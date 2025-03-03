package app;

import jakarta.persistence.EntityManager;
import manager.ManageBook;
import manager.ManageBorrowing;
import table.Book;
import table.Borrowing;
import table.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class UserFrame extends JFrame {
    private final User user;
    private final ManageBook manageBook;
    private final ManageBorrowing manageBorrowing;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Colors
    private final Color PRIMARY_COLOR = new Color(51, 153, 255);
    private final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private final Color ACCENT_COLOR = new Color(255, 255, 255);
    private final Color TEXT_COLOR = Color.BLACK;

    private JTabbedPane tabbedPane;
    private DefaultTableModel allBooksModel;
    private DefaultTableModel borrowingsModel;
    private JTextField searchField;
    private JLabel welcomeLabel;

    public UserFrame(User user, EntityManager entityManager) {
        this.user = user;
        this.manageBook = new ManageBook(entityManager);
        this.manageBorrowing = new ManageBorrowing(entityManager);

        setTitle("Library Management System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(SECONDARY_COLOR);

        initializeComponents();
        loadData();
    }

    private void initializeComponents() {
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(SECONDARY_COLOR);

        // Welcome Panel
        JPanel welcomePanel = createWelcomePanel();
        mainPanel.add(welcomePanel, BorderLayout.NORTH);

        // Initialize tabbed pane with custom styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(ACCENT_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);

        // Create and add tabs
        tabbedPane.addTab("Available Books", createBooksPanel());
        tabbedPane.addTab("My Borrowing History", createBorrowingsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Set up menu bar
        setUpMenuBar();

        add(mainPanel);
    }

    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(PRIMARY_COLOR);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        welcomeLabel = new JLabel("Welcome, " + user.getName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLACK);
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);

        return welcomePanel;
    }

    private JPanel createBooksPanel() {
        JPanel booksPanel = new JPanel(new BorderLayout(10, 10));
        booksPanel.setBackground(ACCENT_COLOR);
        booksPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(ACCENT_COLOR);

        searchField = createStyledTextField();
        JButton searchButton = createStyledButton("Search");
        searchButton.addActionListener(e -> searchBooks());

        JLabel searchLabel = new JLabel("Search Books: ");
        searchLabel.setForeground(Color.BLACK);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Books Table
        String[] bookColumns = {"Title", "Author", "ISBN", "Publisher", "Status"};
        allBooksModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable booksTable = createStyledTable(allBooksModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.getViewport().setBackground(ACCENT_COLOR);

        booksPanel.add(searchPanel, BorderLayout.NORTH);
        booksPanel.add(scrollPane, BorderLayout.CENTER);

        return booksPanel;
    }

    private JPanel createBorrowingsPanel() {
        JPanel borrowingsPanel = new JPanel(new BorderLayout(10, 10));
        borrowingsPanel.setBackground(ACCENT_COLOR);
        borrowingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] borrowingColumns = {"Book Title", "Borrow Date", "Return Date", "Status"};
        borrowingsModel = new DefaultTableModel(borrowingColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable borrowingsTable = createStyledTable(borrowingsModel);
        JScrollPane scrollPane = new JScrollPane(borrowingsTable);
        scrollPane.getViewport().setBackground(ACCENT_COLOR);

        borrowingsPanel.add(scrollPane, BorderLayout.CENTER);

        return borrowingsPanel;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(Color.BLACK); // Ensure the text color is black
        textField.setBackground(Color.WHITE); // Set the background to white for contrast
        textField.setCaretColor(Color.BLACK); // Ensure the caret (cursor) color is black
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(Color.BLACK);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(SECONDARY_COLOR);
        table.setBackground(ACCENT_COLOR);
        table.setSelectionBackground(PRIMARY_COLOR.brighter());
        table.setSelectionForeground(Color.WHITE);

        // Style the header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.BLACK);
        header.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR.darker()));

        return table;
    }

    private void setUpMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PRIMARY_COLOR);
        menuBar.setBorder(null);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.BLACK);
        fileMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JMenuItem refreshItem = createMenuItem("Refresh");
        JMenuItem logoutItem = createMenuItem("Logout");

        refreshItem.addActionListener(e -> loadData());
        logoutItem.addActionListener(e -> handleLogout());

        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuItem.setBackground(ACCENT_COLOR);
        menuItem.setForeground(Color.BLACK);
        return menuItem;
    }

    // Existing methods (loadData, loadAllBooks, loadBorrowingHistory, searchBooks, handleLogout)
    // remain the same but work with the enhanced UI components

    private void loadData() {
        loadAllBooks();
        loadBorrowingHistory();
    }

    private void loadAllBooks() {
        allBooksModel.setRowCount(0);
        List<Book> books = manageBook.getAllBooks();
        for (Book book : books) {
            boolean hasAvailableCopy = book.getCopies().stream()
                    .anyMatch(copy -> "Available".equals(copy.getStatus()));

            Object[] row = {
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getPublisher() != null ? book.getPublisher().getName() : "",
                    hasAvailableCopy ? "Available" : "Not Available"
            };
            allBooksModel.addRow(row);
        }
    }

    private void loadBorrowingHistory() {
        borrowingsModel.setRowCount(0);
        List<Borrowing> borrowings = manageBorrowing.getUserBorrowingHistory(user);
        for (Borrowing borrowing : borrowings) {
            String status = borrowing.getReturnDate() == null ? "Borrowed" : "Returned";
            Object[] row = {
                    borrowing.getCopy().getBook().getTitle(),
                    dateFormat.format(borrowing.getBorrowDate()),
                    borrowing.getReturnDate() != null ? dateFormat.format(borrowing.getReturnDate()) : "-",
                    status
            };
            borrowingsModel.addRow(row);
        }
    }

    private void searchBooks() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllBooks();
            return;
        }

        allBooksModel.setRowCount(0);
        List<Book> books = manageBook.searchBooksByTitle(searchTerm);
        for (Book book : books) {
            boolean hasAvailableCopy = book.getCopies().stream()
                    .anyMatch(copy -> "Available".equals(copy.getStatus()));

            Object[] row = {
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getPublisher() != null ? book.getPublisher().getName() : "",
                    hasAvailableCopy ? "Available" : "Not Available"
            };
            allBooksModel.addRow(row);
        }
    }

    private void handleLogout() {
        dispose();
        new LoginFrame(manageBook.getEntityManager()).setVisible(true);
    }
}