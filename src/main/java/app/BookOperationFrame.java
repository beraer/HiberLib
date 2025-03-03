package app;

import manager.ManageBook;
import manager.ManagePublisher;
import table.Book;
import table.Publisher;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookOperationFrame extends JDialog {
    private final Book book;
    private final ManageBook manageBook;
    private final ManagePublisher managePublisher;
    private boolean bookSaved = false;


    // Form components
    private JTextField titleField;
    private JTextField newPublisherField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField yearField;
    private JComboBox<Publisher> publisherComboBox;

    public BookOperationFrame(JFrame parent, Book book, ManageBook manageBook, ManagePublisher managePublisher) {
        super(parent, book == null ? "Add New Book" : "Edit Book", true);
        this.book = book;
        this.manageBook = manageBook;
        this.managePublisher = managePublisher;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        loadPublishers();
        if (book != null) {
            loadBookData();
        }
    }

    private void initComponents() {
        // Main panel with grid layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        titleField = new JTextField(20);
        mainPanel.add(titleField, gbc);

        // Author
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Author:"), gbc);

        gbc.gridx = 1;
        authorField = new JTextField(20);
        mainPanel.add(authorField, gbc);

        // ISBN
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("ISBN:"), gbc);

        gbc.gridx = 1;
        isbnField = new JTextField(20);
        mainPanel.add(isbnField, gbc);

        // Publication Year
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Publication Year:"), gbc);

        gbc.gridx = 1;
        yearField = new JTextField(20);
        mainPanel.add(yearField, gbc);

        // Publisher Dropdown
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Publisher:"), gbc);

        gbc.gridx = 1;
        publisherComboBox = new JComboBox<>();
        publisherComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Publisher) {
                    setText(((Publisher) value).getName());
                }
                return this;
            }
        });
        mainPanel.add(publisherComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(e -> saveBook());
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadPublishers() {
        List<Publisher> publishers = managePublisher.getAllPublishers();

        // Debug: Print publishers to verify
        System.out.println("Publishers retrieved: " + publishers);

        if (publishers.isEmpty()) {
            System.out.println("No publishers found!");
        }

        for (Publisher publisher : publishers) {
            publisherComboBox.addItem(publisher);
        }
    }


    private void saveBook() {
        if (titleField.getText().trim().isEmpty() || isbnField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and ISBN are required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Book bookToSave = (book != null) ? book : new Book();
            bookToSave.setTitle(titleField.getText().trim());
            bookToSave.setAuthor(authorField.getText().trim());
            bookToSave.setIsbn(isbnField.getText().trim());

            String yearText = yearField.getText().trim();
            if (!yearText.isEmpty()) {
                bookToSave.setPublicationYear(Integer.parseInt(yearText));
            }

            Publisher selectedPublisher = (Publisher) publisherComboBox.getSelectedItem();
            if (selectedPublisher == null) {
                JOptionPane.showMessageDialog(this, "Please select a publisher.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            bookToSave.setPublisher(selectedPublisher);

            if (book == null) {
                manageBook.createBook(bookToSave);
            } else {
                manageBook.updateBook(bookToSave);
            }
            bookSaved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBookData() {
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        if (book.getPublicationYear() != null) {
            yearField.setText(book.getPublicationYear().toString());
        }

        Publisher bookPublisher = book.getPublisher();
        if (bookPublisher != null) {
            for (int i = 0; i < publisherComboBox.getItemCount(); i++) {
                Publisher publisher = publisherComboBox.getItemAt(i);
                if (publisher.getId().equals(bookPublisher.getId())) {
                    publisherComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public boolean isBookSaved() {
        return bookSaved;
    }
}