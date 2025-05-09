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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/bankdb2";
        String user = "root"; // replace with your MySQL username
        String pass = "password"; // replace with your MySQL password
        return DriverManager.getConnection(url, user, pass);
    }
}

