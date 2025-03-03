package app;

import jakarta.persistence.EntityManager;
import manager.TestData;
import manager.ManageLibrarian;
import manager.ManageUser;
import table.User;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LoginFrame extends JFrame {
    private final ManageUser userService;
    private final ManageLibrarian manageLibrarian;
    private final EntityManager entityManager;

    private JTextField emailField;
    private JLabel statusLabel;
    private JPanel mainPanel;
    private Color primaryColor = new Color(66, 133, 244);
    private Color lightGray = new Color(240, 240, 240);
    private Color darkGray = new Color(80, 80, 80);

    public LoginFrame(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.userService = new ManageUser(entityManager);
        this.manageLibrarian = new ManageLibrarian(entityManager);

        // Initialize database with test data
        TestData initializer = new TestData(entityManager);
        initializer.initializeDatabase();

        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        customizeComponents();
    }

    private void initComponents() {
        // Main panel with custom background
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(66, 133, 244, 50),
                        getWidth(), getHeight(), new Color(255, 255, 255, 100));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        // Logo/Title Section
        JLabel logoLabel = new JLabel("📚", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(logoLabel, gbc);

        JLabel titleLabel = new JLabel("Library Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        gbc.insets = new Insets(0, 0, 30, 0);
        mainPanel.add(subtitleLabel, gbc);

        // Email Field
        emailField = createStyledTextField("Enter your email");
        gbc.insets = new Insets(5, 0, 15, 0);
        mainPanel.add(emailField, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.addActionListener(e -> handleLogin());
        mainPanel.add(loginButton, gbc);

        // Status Label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(220, 53, 69));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.insets = new Insets(15, 0, 5, 0);
        mainPanel.add(statusLabel, gbc);

        // Demo Accounts Section
        gbc.insets = new Insets(30, 0, 5, 0);
        JLabel demoLabel = new JLabel("Demo Accounts", SwingConstants.CENTER);
        demoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        demoLabel.setForeground(Color.black);
        mainPanel.add(demoLabel, gbc);

        String[] demoAccounts = {
                "📧 user@library.com",
                "📧 librarian@library.com",
                "📧 senior@library.com"
        };

        for (String account : demoAccounts) {
            JLabel accountLabel = new JLabel(account, SwingConstants.CENTER);
            accountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            accountLabel.setForeground(Color.black);
            gbc.insets = new Insets(2, 0, 2, 0);
            mainPanel.add(accountLabel, gbc);
        }

        add(mainPanel);
    }

    private void customizeComponents() {
        // Add window drop shadow (if supported)
        if (isWindows()) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            getRootPane().putClientProperty("JRootPane.titleBarBackground", Color.WHITE);
            getRootPane().putClientProperty("JRootPane.titleBarForeground", darkGray);
        }
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.setFont(getFont().deriveFont(Font.PLAIN));
                    g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getAscent() + getInsets().top);
                }
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.black);
        field.setBackground(lightGray);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primaryColor),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });

        return field;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.black);
        button.setBackground(primaryColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(primaryColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(primaryColor);
            }
        });
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private void handleLogin() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showError("Please enter your email");
            return;
        }

        try {
            userService.findUserByEmail(email).ifPresentOrElse(
                    user -> {
                        boolean isLibrarian = manageLibrarian.isLibrarian(user);
                        openMainWindow(user, isLibrarian);
                        dispose();
                    },
                    () -> showError("User not found")
            );
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        // Shake animation for error
        Point originalLocation = mainPanel.getLocation();
        Timer timer = new Timer(30, null);
        final int[] moves = {-5, 5, -5, 5, -5, 5, -3, 3, -2, 2, -1, 1};
        final int[] index = {0};

        timer.addActionListener(e -> {
            if (index[0] < moves.length) {
                mainPanel.setLocation(originalLocation.x + moves[index[0]], originalLocation.y);
                index[0]++;
            } else {
                mainPanel.setLocation(originalLocation);
                timer.stop();
            }
        });
        timer.start();
    }

    private void openMainWindow(User user, boolean isLibrarian) {
        SwingUtilities.invokeLater(() -> {
            if (isLibrarian) {
                LibrarianFrame librarianFrame = new LibrarianFrame(user, entityManager);
                librarianFrame.setVisible(true);
            } else {
                UserFrame userFrame = new UserFrame(user, entityManager);
                userFrame.setVisible(true);
            }
        });
    }
}