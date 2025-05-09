/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bankingsystem2;

/**
 *
 * @author aarad
 */
//package com.mycompany.bankingsystem2;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;

public class CustomerUI extends JFrame {
    private int accountId;
    private JTextField amountField;
    private JLabel statusLabel, balanceLabel;
    private AccountDAO accountDAO = new AccountDAO();

    public CustomerUI(int accountId) {
        this.accountId = accountId;
        setTitle("Customer Panel");
        setSize(450, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set background color for the frame
        getContentPane().setBackground(new Color(245, 248, 255));

        // Main panel with padding and vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(245, 248, 255));

        // Title
        JLabel titleLabel = new JLabel("Welcome to Your Account", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(33, 70, 120));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Amount panel
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.setOpaque(false);
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        amountField = new JTextField(12);
        amountField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        amountField.setBackground(Color.WHITE);
        amountField.setForeground(Color.BLACK);
        amountPanel.add(amountLabel);
        amountPanel.add(amountField);
        mainPanel.add(amountPanel);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        buttonPanel.setOpaque(false);
        Color buttonColor = new Color(110, 150, 180);

        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton checkBalanceBtn = new JButton("Check Balance");
        JButton viewTransactionsBtn = new JButton("View Transactions");

        JButton[] buttons = {depositBtn, withdrawBtn, checkBalanceBtn, viewTransactionsBtn};
        for (JButton btn : buttons) {
            btn.setBackground(buttonColor);
            btn.setForeground(Color.BLUE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("SansSerif", Font.BOLD, 14));
            btn.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        }

        depositBtn.setToolTipText("Deposit money into your account");
        withdrawBtn.setToolTipText("Withdraw money from your account");
        checkBalanceBtn.setToolTipText("Check your current account balance");
        viewTransactionsBtn.setToolTipText("View your transaction history");

        buttonPanel.add(depositBtn);
        buttonPanel.add(withdrawBtn);
        buttonPanel.add(checkBalanceBtn);
        buttonPanel.add(viewTransactionsBtn);
        mainPanel.add(buttonPanel);

        mainPanel.add(Box.createVerticalStrut(15));

        // Balance label
        balanceLabel = new JLabel("Balance: ");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        balanceLabel.setForeground(new Color(33, 70, 120));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(balanceLabel);

        // Status label at the bottom
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        statusLabel.setForeground(new Color(150, 0, 0));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(statusLabel);

        add(mainPanel);

        // Button actions
        depositBtn.addActionListener(e -> performDeposit());
        withdrawBtn.addActionListener(e -> performWithdraw());
        checkBalanceBtn.addActionListener(e -> performCheckBalance());
        viewTransactionsBtn.addActionListener(e -> showTransactionTable());
    }


    private void performDeposit() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            accountDAO.deposit(accountId, amount);
            statusLabel.setText("Deposited successfully.");
           

            performCheckBalance();
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void performWithdraw() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            accountDAO.withdraw(accountId, amount);
            statusLabel.setText("Withdrawn successfully.");
            performCheckBalance();
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void performCheckBalance() {
        try {
            double balance = accountDAO.getBalance(accountId);
            balanceLabel.setText("Balance: " + balance);
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

   private void showTransactionTable() {
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(
             "SELECT t.type, t.amount, t.date, t.counterparty_account_id, a.name AS counterparty_name " +
             "FROM transactions t LEFT JOIN accounts a ON t.counterparty_account_id = a.id " +
             "WHERE t.account_id = ? ORDER BY t.date DESC"
         )) {
        ps.setInt(1, accountId);
        ResultSet rs = ps.executeQuery();

        String[] columnNames = {"Type", "Amount", "Date", "Counterparty ID", "Counterparty Name"};
        java.util.List<Object[]> data = new java.util.ArrayList<>();
        while (rs.next()) {
            data.add(new Object[]{
                    rs.getString("type"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("date"),
                    rs.getObject("counterparty_account_id"),
                    rs.getString("counterparty_name")
            });
        }
        Object[][] rowData = data.toArray(new Object[0][]);
        JTable table = new JTable(rowData, columnNames);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new java.awt.Dimension(800, 200));
        JOptionPane.showMessageDialog(this, scrollPane, "Transaction History", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
        statusLabel.setText("Error: " + ex.getMessage());
    }
}

}

