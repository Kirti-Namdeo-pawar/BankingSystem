package com.mycompany.bankingsystem2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // Create account
    public int createAccount(String name, String dob, String address, String phone,
                             String email, String idNumber, String accountType,
                             String gender, double balance) throws SQLException {
        String sql = "INSERT INTO accounts (name, dob, address, phone, email, id_number, account_type, gender, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setDate(2, Date.valueOf(dob));
            ps.setString(3, address);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setString(6, idNumber);
            ps.setString(7, accountType);
            ps.setString(8, gender);
            ps.setDouble(9, balance);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Account creation failed: No ID obtained");
    }

    // Deposit
    public void deposit(int accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
        addTransaction(accountId, "Deposit", amount);
    }

    // Withdraw
    public void withdraw(int accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, accountId);
            ps.setDouble(3, amount);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Insufficient funds or account not found");
        }
        addTransaction(accountId, "Withdraw", amount);
    }

    // Get balance
    public double getBalance(int accountId) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("balance");
                else throw new SQLException("Account not found");
            }
        }
    }

    // Add transaction record (used by deposit/withdraw/transfer)
    private void addTransaction(int accountId, String type, double amount) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, type, amount) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        }
    }

    // Get all accounts (for dashboard)
    public List<Object[]> getAllAccounts() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, name, dob, address, phone, email, id_number, account_type, gender, balance FROM accounts";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("dob"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("id_number"),
                        rs.getString("account_type"),
                        rs.getString("gender"),
                        rs.getDouble("balance")
                });
            }
        }
        return list;
    }

    // Update account details
    public void updateAccount(int id, String name, String dob, String address,
                              String phone, String email, String idNumber,
                              String accountType, String gender) throws SQLException {
        String sql = "UPDATE accounts SET name=?, dob=?, address=?, phone=?, email=?, id_number=?, account_type=?, gender=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDate(2, Date.valueOf(dob));
            ps.setString(3, address);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setString(6, idNumber);
            ps.setString(7, accountType);
            ps.setString(8, gender);
            ps.setInt(9, id);
            ps.executeUpdate();
        }
    }

    // Delete account (delete transactions and users first due to FK constraints)
    public void deleteAccount(int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete transactions
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM transactions WHERE account_id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                // Delete users
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE account_id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                // Delete account
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM accounts WHERE id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // Transfer funds (with transaction records)
    public void transferFunds(int fromId, int toId, double amount) throws SQLException {
        String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        String depositSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Withdraw from source
            try (PreparedStatement withdrawPs = conn.prepareStatement(withdrawSql)) {
                withdrawPs.setDouble(1, amount);
                withdrawPs.setInt(2, fromId);
                withdrawPs.setDouble(3, amount);
                int rows = withdrawPs.executeUpdate();
                if (rows == 0) throw new SQLException("Insufficient funds or source account not found.");
            }

            // Deposit to destination
            try (PreparedStatement depositPs = conn.prepareStatement(depositSql)) {
                depositPs.setDouble(1, amount);
                depositPs.setInt(2, toId);
                int rows = depositPs.executeUpdate();
                if (rows == 0) throw new SQLException("Destination account not found.");
            }

            // Add transactions
            addTransactionWithConn(conn, fromId, "Transfer Out", amount, toId);
addTransactionWithConn(conn, toId, "Transfer In", amount, fromId);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
            if (conn != null) conn.close();
        }
    }

    // Add transaction using an existing connection (for transferFunds)
    private void addTransactionWithConn(Connection conn, int accountId, String type, double amount, Integer counterpartyId) throws SQLException {
    String sql = "INSERT INTO transactions (account_id, type, amount, counterparty_account_id) VALUES (?, ?, ?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, accountId);
        ps.setString(2, type);
        ps.setDouble(3, amount);
        if (counterpartyId != null)
            ps.setInt(4, counterpartyId);
        else
            ps.setNull(4, java.sql.Types.INTEGER);
        ps.executeUpdate();
    }
}

// In transferFunds:


}
