package com.mycompany.bankingsystem2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginUI extends JFrame {
    private JLabel statusLabel;

    public LoginUI() {
        setTitle("Login");
        setSize(400, 260);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set content pane background
        getContentPane().setBackground(new Color(230, 240, 250));

        // Main panel with padding and vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(new Color(230, 240, 250));

        // Logo (optional)
        // Uncomment and update path if you have a logo image
        // JLabel logoLabel = new JLabel(new ImageIcon("bank_logo.png"));
        // logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // mainPanel.add(logoLabel);

        // Title
        JLabel titleLabel = new JLabel("Banking System Login", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 70, 120));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        usernameField.setToolTipText("Enter your username");

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        passwordField.setToolTipText("Enter your password");

        formPanel.add(userLabel);
        formPanel.add(usernameField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(200, 150, 180));
        loginBtn.setForeground(Color.BLUE);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        loginBtn.setFocusPainted(false);
        loginBtn.setToolTipText("Login to your account");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(loginBtn);
        mainPanel.add(Box.createVerticalStrut(10));

        // Status label for errors
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        statusLabel.setForeground(new Color(150, 0, 0));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);

        add(mainPanel);

        // Button action
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            try {
                User user = new UserDAO().authenticate(username, password);
                if (user != null) {
                    dispose();
                    if ("admin".equals(user.role)) {
                        new AdminUI().setVisible(true);
                    } else {
                        new CustomerUI(user.accountId).setVisible(true);
                    }
                } else {
                    statusLabel.setText("Invalid credentials!");
                }
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        // Modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore
        }
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
