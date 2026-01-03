package com.stokbizde;

import javax.swing.*;
import java.awt.*;

/**
 * Stokbizde Ana Uygulama Sınıfı
 * Stok yönetim sistemi için temel Java Swing uygulaması
 */
public class StokbizdeApp extends JFrame {

    public StokbizdeApp() {
        initializeUI();
    }

    private void initializeUI() {
        // Ana pencere ayarları
        setTitle("Stokbizde - Stok Yönetim Sistemi");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ekranın ortasında aç

        // Menü çubuğunu oluştur
        createMenuBar();

        // Ana layout'u ayarla
        setLayout(new BorderLayout());

        // Hoş geldiniz paneli oluştur
        JPanel welcomePanel = createWelcomePanel();
        add(welcomePanel, BorderLayout.CENTER);

        // Durum çubuğu ekle
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Dosya menüsü
        JMenu fileMenu = new JMenu("Dosya");
        JMenuItem newItem = new JMenuItem("Yeni");
        JMenuItem openItem = new JMenuItem("Aç");
        JMenuItem saveItem = new JMenuItem("Kaydet");
        JMenuItem exitItem = new JMenuItem("Çıkış");
        
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Düzen menüsü
        JMenu editMenu = new JMenu("Düzen");
        JMenuItem cutItem = new JMenuItem("Kes");
        JMenuItem copyItem = new JMenuItem("Kopyala");
        JMenuItem pasteItem = new JMenuItem("Yapıştır");
        
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);

        // Yardım menüsü
        JMenu helpMenu = new JMenu("Yardım");
        JMenuItem aboutItem = new JMenuItem("Hakkında");
        JMenuItem helpItem = new JMenuItem("Yardım Konuları");
        
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        // Menüleri menü çubuğuna ekle
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Başlık
        JLabel titleLabel = new JLabel("Stokbizde'ye Hoş Geldiniz");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        JLabel subtitleLabel = new JLabel("Stok Yönetim Sistemi");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(subtitleLabel, gbc);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel("Hazır");
        statusBar.add(statusLabel);

        return statusBar;
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(
            this,
            "Stokbizde v1.0\nStok Yönetim Sistemi\n\n© 2025 Selçuk Kurak",
            "Hakkında",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        // Swing uygulamalarını Event Dispatch Thread (EDT) üzerinde çalıştır
        SwingUtilities.invokeLater(() -> {
            try {
                // System look and feel kullan
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Look and feel yüklenemezse varsayılan ile devam et
                System.err.println("Uyarı: Sistem görünümü yüklenemedi. Varsayılan görünüm kullanılacak.");
            }

            StokbizdeApp app = new StokbizdeApp();
            app.setVisible(true);
        });
    }
}
