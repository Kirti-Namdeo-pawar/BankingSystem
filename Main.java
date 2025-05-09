package com.mycompany.bankingsystem2;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Set system look and feel for modern appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If look and feel fails, print error but continue
            e.printStackTrace();
        }
        // Always launch Swing UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
