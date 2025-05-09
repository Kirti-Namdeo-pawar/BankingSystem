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
import java.util.ArrayList;
import java.util.List;

public class AdminUI extends JFrame {
    private AccountDAO accountDAO = new AccountDAO();

    public AdminUI() {
        setTitle("Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton dashboardBtn = new JButton("View Dashboard");
        JButton createAccountBtn = new JButton("Create Account");
        JButton createUserBtn = new JButton("Create User");
        JButton updateAccountBtn = new JButton("Update Account");
        JButton deleteAccountBtn = new JButton("Delete Account");
        JButton transferFundsBtn = new JButton("Transfer Funds");

        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15));
        panel.add(dashboardBtn);
        panel.add(createAccountBtn);
        panel.add(createUserBtn);
        panel.add(updateAccountBtn);
        panel.add(deleteAccountBtn);
        panel.add(transferFundsBtn);

        add(panel);

        dashboardBtn.addActionListener(e -> showDashboard());
        createAccountBtn.addActionListener(e -> new CreateAccountForm().setVisible(true));
       // createUserBtn.addActionListener(e -> new CreateAccountFUI(this).setVisible(true));
        updateAccountBtn.addActionListener(e -> updateAccountDialog());
        deleteAccountBtn.addActionListener(e -> deleteAccountDialog());
        transferFundsBtn.addActionListener(e -> transferFundsDialog());
    }

    private void showDashboard() {
    try (Connection conn = DBUtil.getConnection()) {
        // Improved Accounts Table with more info
        String sql = "SELECT a.id AS account_id, u.id AS user_id, a.name, a.phone, a.address, a.account_type, a.dob AS joining_date, a.balance " +
                     "FROM accounts a LEFT JOIN users u ON u.account_id = a.id";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
JPanel formPanel = new JPanel(new GridLayout(0,2));
formPanel.setBorder(BorderFactory.createTitledBorder("Account Details"));

        String[] columns = {"Account ID", "User ID", "Name", "Phone", "Address", "Account Type", "Joining Date", "Balance"};
        List<Object[]> data = new ArrayList<>();
        double totalBalance = 0;
        while (rs.next()) {
            int accountId = rs.getInt("account_id");
            Object userId = rs.getObject("user_id"); // may be null
            String name = rs.getString("name");
            String phone = rs.getString("phone");
            String address = rs.getString("address");
            String accountType = rs.getString("account_type");
            Date joiningDate = rs.getDate("joining_date");
            double balance = rs.getDouble("balance");
            totalBalance += balance;
            data.add(new Object[]{accountId, userId, name, phone, address, accountType, joiningDate, balance});
        }
        Object[][] rows = data.toArray(new Object[0][]);
        JTable table = new JTable(rows, columns);
table.setRowHeight(25);
table.setFont(new Font("SansSerif", Font.PLAIN, 14));
table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));

        JPanel dashboardPanel = new JPanel(new BorderLayout(10, 10));
        dashboardPanel.add(new JLabel("All Accounts (Total Balance: " + totalBalance + ")"), BorderLayout.NORTH);
        dashboardPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Transactions Button
        JButton showTransactionsBtn = new JButton("Show All Transactions");
        dashboardPanel.add(showTransactionsBtn, BorderLayout.SOUTH);

        showTransactionsBtn.addActionListener(ev -> {
            try (PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT t.account_id, t.type, t.amount, t.date, t.counterparty_account_id, a.name AS counterparty_name " +
                    "FROM transactions t LEFT JOIN accounts a ON t.counterparty_account_id = a.id " +
                    "ORDER BY t.date DESC")) {
                ResultSet trs = ps2.executeQuery();
                String[] tcols = {"Account ID", "Type", "Amount", "Date", "Counterparty ID", "Counterparty Name"};
                List<Object[]> tdata = new ArrayList<>();
                while (trs.next()) {
                    tdata.add(new Object[]{
                            trs.getInt("account_id"),
                            trs.getString("type"),
                            trs.getDouble("amount"),
                            trs.getTimestamp("date"),
                            trs.getObject("counterparty_account_id"),
                            trs.getString("counterparty_name")
                    });
                }
                Object[][] trows = tdata.toArray(new Object[0][]);
                JTable ttable = new JTable(trows, tcols);

                JScrollPane scrollPane = new JScrollPane(ttable);
                scrollPane.setPreferredSize(new Dimension(800, 300));
                JOptionPane.showMessageDialog(this, scrollPane, "All Transaction Records", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JOptionPane.showMessageDialog(this, dashboardPanel, "Dashboard", JOptionPane.PLAIN_MESSAGE);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}


    // Update Account Dialog
    private void updateAccountDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Account ID to Update:");
        if (idStr == null) return;
        try {
            int id = Integer.parseInt(idStr);
            // Fetch current details
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM accounts WHERE id=?")) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JTextField nameField = new JTextField(rs.getString("name"));
                    JTextField dobField = new JTextField(rs.getString("dob"));
                    JTextField addressField = new JTextField(rs.getString("address"));
                    JTextField phoneField = new JTextField(rs.getString("phone"));
                    JTextField emailField = new JTextField(rs.getString("email"));
                    JTextField idNumberField = new JTextField(rs.getString("id_number"));
                    JTextField accountTypeField = new JTextField(rs.getString("account_type"));
                    JTextField genderField = new JTextField(rs.getString("gender"));

                    JPanel panel = new JPanel(new GridLayout(0, 2));
                    panel.add(new JLabel("Name:")); panel.add(nameField);
                    panel.add(new JLabel("DOB:")); panel.add(dobField);
                    panel.add(new JLabel("Address:")); panel.add(addressField);
                    panel.add(new JLabel("Phone:")); panel.add(phoneField);
                    panel.add(new JLabel("Email:")); panel.add(emailField);
                    panel.add(new JLabel("ID Number:")); panel.add(idNumberField);
                    panel.add(new JLabel("Account Type:")); panel.add(accountTypeField);
                    panel.add(new JLabel("Gender:")); panel.add(genderField);

                    int result = JOptionPane.showConfirmDialog(this, panel, "Update Account", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        accountDAO.updateAccount(id, nameField.getText(), dobField.getText(), addressField.getText(),
                                phoneField.getText(), emailField.getText(), idNumberField.getText(),
                                accountTypeField.getText(), genderField.getText());
                        JOptionPane.showMessageDialog(this, "Account updated.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Account not found.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Delete Account Dialog
    private void deleteAccountDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Account ID to Delete:");
        if (idStr == null) return;
        try {
            int id = Integer.parseInt(idStr);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete account " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                accountDAO.deleteAccount(id);
                JOptionPane.showMessageDialog(this, "Account deleted.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Transfer Funds Dialog
    private void transferFundsDialog() {
        JTextField fromField = new JTextField();
        JTextField toField = new JTextField();
        JTextField amountField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("From Account ID:")); panel.add(fromField);
        panel.add(new JLabel("To Account ID:")); panel.add(toField);
        panel.add(new JLabel("Amount:")); panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Transfer Funds", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int fromId = Integer.parseInt(fromField.getText());
                int toId = Integer.parseInt(toField.getText());
                double amount = Double.parseDouble(amountField.getText());
                accountDAO.transferFunds(fromId, toId, amount);
                JOptionPane.showMessageDialog(this, "Transfer successful.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}

