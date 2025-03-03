package app;

import manager.ManageCopy;
import table.Book;
import table.Copy;

import javax.swing.*;
import java.awt.*;

public class CopyFrame extends JDialog {
    private final Copy copy;
    private final ManageCopy manageCopy;
    private final Book book;
    private boolean copySaved = false;

    private JTextField copyNumberField;
    private JComboBox<String> statusComboBox;

    public CopyFrame(JDialog parent, Copy copy, ManageCopy manageCopy, Book book) {
        super(parent, copy == null ? "Add New Copy" : "Edit Copy", true);
        this.copy = copy;
        this.manageCopy = manageCopy;
        this.book = book;

        setSize(400, 200);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        if (copy != null) {
            loadCopyData();
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Copy Number
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Copy Number:"), gbc);

        gbc.gridx = 1;
        copyNumberField = new JTextField(20);
        mainPanel.add(copyNumberField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        String[] statuses = {"Available", "Borrowed", "Withdrawn"};
        statusComboBox = new JComboBox<>(statuses);
        mainPanel.add(statusComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveCopy());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCopyData() {
        copyNumberField.setText(copy.getCopyNumber().toString());
        statusComboBox.setSelectedItem(copy.getStatus());
    }

    private void saveCopy() {
        String copyNumberText = copyNumberField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();

        if (copyNumberText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Copy number is required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Integer copyNumber = Integer.parseInt(copyNumberText);

            if (copy == null) {
                // Adding new copy
                manageCopy.createCopy(book, copyNumber);
            } else {
                // Editing existing copy
                copy.setCopyNumber(copyNumber);
                manageCopy.updateCopyStatus(copy, status);
            }

            copySaved = true;
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Copy number must be a valid integer.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving copy: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCopySaved() {
        return copySaved;
    }
}
