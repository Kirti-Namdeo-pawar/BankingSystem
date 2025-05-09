/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bankingsystem2;

/**
 *
 * @author aarad
 */
// CreateAccountForm.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreateAccountForm extends JFrame {
    private JTextField nameField, dobField, addressField, phoneField, emailField, idNumberField, usernameField;
    private JComboBox<String> accountTypeBox, genderBox;
    private JPasswordField passwordField;
    private JTextField balanceField;
    private JButton submitButton;

    public CreateAccountForm() {
        setTitle("Create Account and User");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(12, 2));

        nameField = new JTextField();
        dobField = new JTextField("YYYY-MM-DD");
        addressField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        idNumberField = new JTextField();
        accountTypeBox = new JComboBox<>(new String[]{"savings", "current"});
        genderBox = new JComboBox<>(new String[]{"male", "female", "other"});
        balanceField = new JTextField();

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        submitButton = new JButton("Create Account and User");

        add(new JLabel("Name")); add(nameField);
        add(new JLabel("DOB")); add(dobField);
        add(new JLabel("Address")); add(addressField);
        add(new JLabel("Phone")); add(phoneField);
        add(new JLabel("Email")); add(emailField);
        add(new JLabel("ID Number")); add(idNumberField);
        add(new JLabel("Account Type")); add(accountTypeBox);
        add(new JLabel("Gender")); add(genderBox);
        add(new JLabel("Initial Balance")); add(balanceField);

        add(new JLabel("Username")); add(usernameField);
        add(new JLabel("Password")); add(passwordField);

        add(new JLabel("")); add(submitButton);

        submitButton.addActionListener(e -> handleSubmit());
    }

    private void handleSubmit() {
        try {
            String name = nameField.getText();
            String dob = dobField.getText();
            String address = addressField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String idNumber = idNumberField.getText();
            String accountType = (String) accountTypeBox.getSelectedItem();
            String gender = (String) genderBox.getSelectedItem();
            double balance = Double.parseDouble(balanceField.getText());

            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
AccountDAO accountDAO = new AccountDAO();
UserDAO userDAO = new UserDAO();
            int accountId = accountDAO.createAccount(name, dob, address, phone, email, idNumber, accountType, gender, balance);
            if (accountId > 0) {
                boolean userCreated = userDAO.createUserForAccount(username, password, accountId);
                if (userCreated) {
                    JOptionPane.showMessageDialog(this, "Account and User created successfully.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Account created but failed to create user.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create account.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CreateAccountForm().setVisible(true));
    }
}
