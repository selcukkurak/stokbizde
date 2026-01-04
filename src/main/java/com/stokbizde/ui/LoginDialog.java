package com.stokbizde.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.stokbizde.dao.UserDAO;
import com.stokbizde.model.User;
import com.stokbizde.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private boolean loginSuccessful = false;
    private UserDAO userDAO;

    public LoginDialog(Frame parent) {
        super(parent, "Giriş Yap - Stokbizde", true);
        this.userDAO = new UserDAO();

        // İlk admin kullanıcısını oluştur
        userDAO.createDefaultAdmin();

        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(500, 650);
        setResizable(false);

        // Üst panel - Logo ve başlık
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 30, 40));

        JLabel titleLabel = new JLabel("STOKBİZDE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(41, 98, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Stok Yönetim Sistemi");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Hoş Geldiniz");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(new Color(120, 120, 120));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        topPanel.add(subtitleLabel);
        topPanel.add(welcomeLabel);

        // Orta panel - Form
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Kullanıcı adı
        JLabel usernameLabel = new JLabel("Kullanıcı Adı");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Kullanıcı adınızı girin");
        usernameField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        // Şifre
        JLabel passwordLabel = new JLabel("Şifre");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Şifrenizi girin");
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");

        // Enter tuşu ile giriş
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // Giriş butonu
        loginButton = new JButton("Giriş Yap");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setBackground(new Color(41, 98, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> performLogin());

        // İptal butonu
        cancelButton = new JButton("İptal");
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(new Color(245, 245, 245));
        cancelButton.setForeground(new Color(100, 100, 100));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> {
            loginSuccessful = false;
            dispose();
        });

        centerPanel.add(usernameLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        centerPanel.add(usernameField);
        centerPanel.add(passwordLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        centerPanel.add(passwordField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        centerPanel.add(loginButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(cancelButton);

        // Alt panel - Bilgi
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(245, 247, 250));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 40, 20, 40));

        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<span style='color: #666; font-size: 11px;'>Varsayılan Giriş: admin / admin123</span><br>" +
                "<span style='color: #999; font-size: 10px;'>© 2026 Stokbizde - Tüm hakları saklıdır</span>" +
                "</div></html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(infoLabel);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("Lütfen kullanıcı adınızı girin.");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Lütfen şifrenizi girin.");
            passwordField.requestFocus();
            return;
        }

        // Giriş butonunu devre dışı bırak
        loginButton.setEnabled(false);
        loginButton.setText("Giriş yapılıyor...");

        // Arka planda giriş işlemi
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.authenticate(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        SessionManager.getInstance().login(user);
                        loginSuccessful = true;
                        dispose();
                    } else {
                        showError("Kullanıcı adı veya şifre hatalı!");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception ex) {
                    showError("Giriş sırasında bir hata oluştu: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Giriş Yap");
                }
            }
        };
        worker.execute();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Hata", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public static boolean showLoginDialog(Frame parent) {
        LoginDialog dialog = new LoginDialog(parent);
        dialog.setVisible(true);
        return dialog.isLoginSuccessful();
    }
}

