package app;

import manager.ManageUser;
import table.User;

import javax.swing.*;
import java.awt.*;

public class UserOperationFrame extends JDialog {
    private final User user;
    private final ManageUser userService;
    private boolean userSaved = false;

    // Form components
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;

    public UserOperationFrame(JFrame parent, User user, ManageUser userService) {
        super(parent, user == null ? "Add New User" : "Edit User", true);
        this.user = user;
        this.userService = userService;

        setSize(400, 350);  // Increased height to accommodate new field
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        if (user != null) {
            loadUserData();
        }
    }

    private void initComponents() {
        // Main panel with grid layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        mainPanel.add(emailField, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        phoneField = new JTextField(20);
        mainPanel.add(phoneField, gbc);

        // Address field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        addressField = new JTextField(20);
        mainPanel.add(addressField, gbc);


        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveUser());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUserData() {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhoneNumber());
        addressField.setText(user.getAddress());
    }

    private void saveUser() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name and Email are required fields.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Create or update user object
            User userToSave = (user != null) ? user : new User();
            userToSave.setName(nameField.getText().trim());
            userToSave.setEmail(emailField.getText().trim());
            userToSave.setPhoneNumber(phoneField.getText().trim());
            userToSave.setAddress(addressField.getText().trim());

            // Save user
            if (user == null) {
                userService.registerUser(userToSave);
            } else {
                userService.registerUser(userToSave);
            }

            userSaved = true;
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving user: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUserSaved() {
        return userSaved;
    }
}