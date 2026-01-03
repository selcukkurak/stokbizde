package com.stokbizde;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Stokbizde - Stok Yönetim Sistemi");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            // Menü çubuğu
            JMenuBar menuBar = new JMenuBar();

            // Dosya menüsü
            JMenu fileMenu = new JMenu("Dosya");
            JMenuItem exitItem = new JMenuItem("Çıkış");
            exitItem.addActionListener(e -> System.exit(0));
            fileMenu.add(exitItem);
            menuBar.add(fileMenu);

            // Düzen menüsü (placeholder)
            JMenu editMenu = new JMenu("Düzen");
            menuBar.add(editMenu);

            // Yardım menüsü
            JMenu helpMenu = new JMenu("Yardım");
            JMenuItem aboutItem = new JMenuItem("Hakkında");
            aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Stokbizde v1.0"));
            helpMenu.add(aboutItem);
            menuBar.add(helpMenu);

            frame.setJMenuBar(menuBar);

            // Ana panel
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(new JLabel("Hoşgeldiniz! Stok yönetim sistemi başlatıldı.", SwingConstants.CENTER), BorderLayout.CENTER);
            frame.add(mainPanel);

            frame.setVisible(true);
        });
    }
}
