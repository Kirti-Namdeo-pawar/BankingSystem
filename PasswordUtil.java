package com.mycompany.bankingsystem2;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author aarad
 */
//package com.bankingsystem2;

import java.security.MessageDigest;

public class PasswordUtil {
    // Method to hash the password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Method to check if the provided password matches the stored hashed password
    public static boolean checkPassword(String password, String storedHash) {
        // Hash the input password and compare with the stored hash
        String hashedPassword = hashPassword(password);
        return hashedPassword.equals(storedHash);
    }
}


