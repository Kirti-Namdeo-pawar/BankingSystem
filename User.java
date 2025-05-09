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

public class User {
    public int id;
    public String username;
    public String role;
    public Integer accountId; // null for admin

    public User(int id, String username, String role, Integer accountId) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.accountId = accountId;
    }
}
