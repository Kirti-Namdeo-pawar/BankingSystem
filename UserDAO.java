/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bankingsystem2;

/**
 *
 * @author aarad
 */
//package com.bankingsystem2;

import java.sql.*;

public class UserDAO {
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String role = rs.getString("role");
                    Integer accountId = rs.getObject("account_id") != null ? rs.getInt("account_id") : null;
                    if (storedHash.equals(PasswordUtil.hashPassword(password))) {
                        return new User(rs.getInt("id"), username, role, accountId);
                    }
                }
            }
        }
        return null;
    }
    
     public static boolean createUserForAccount(String username, String password, int accountId) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, account_id) VALUES (?, ?, 'customer', ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hashPassword(password));
            ps.setInt(3, accountId);
            return ps.executeUpdate() > 0;
        }
    }
     
     // In UserDAO.java
public static boolean validateLogin(String username, String password) throws SQLException {
    String sql = "SELECT password_hash FROM users WHERE username = ?";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, username);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return PasswordUtil.checkPassword(password, storedHash);
            }
        }
    }
    return false;
}

}

