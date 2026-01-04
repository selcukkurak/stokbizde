package com.stokbizde;

import com.formdev.flatlaf.FlatLightLaf;
import com.kitfox.svg.app.beans.SVGIcon;
import com.stokbizde.model.User;
import com.stokbizde.service.InitializationService;
import com.stokbizde.ui.BranchManagementPanel;
import com.stokbizde.ui.CashRegisterManagementPanel;
import com.stokbizde.ui.CompanyInfoPanel;
import com.stokbizde.ui.CompanySetupWizard;
import com.stokbizde.ui.LocationManagementPanel;
import com.stokbizde.ui.LoginDialog;
import com.stokbizde.ui.TransferManagementPanel;
import com.stokbizde.ui.UserManagementPanel;
import com.stokbizde.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainApp {
    private static JFrame mainFrame;
    private static JPanel centerPanel;
    private static JLabel statusLabel;

    // Development mode - test için direkt giriş
    private static final boolean DEVELOPMENT_MODE = false;

    public static void main(String[] args) {
        // Modern FlatLaf teması
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Uygulama başlangıcında adres verilerini kontrol et ve gerekirse arka planda yükle
        // Bu işlem asenkron çalışır, UI'ı bloklamaz
        new Thread(() -> {
            InitializationService initService = new InitializationService();
            initService.initializeAddressData();
        }).start();

        SwingUtilities.invokeLater(() -> {
            // Önce şirket bilgilerinin kurulumunu kontrol et
            InitializationService initService = new InitializationService();
            if (!initService.isCompanySetupCompleted()) {
                // İlk kurulum yapılmamış, kurulum wizard'ını göster
                showCompanySetupWizard();
                return;
            }

            // Development mode kontrolü
            if (DEVELOPMENT_MODE) {
                // Test kullanıcısı oluştur ve direkt giriş yap
                User testUser = new User();
                testUser.setId("dev-user");
                testUser.setUsername("admin");
                testUser.setFullName("Test Admin (DEV MODE)");
                testUser.setRole(User.Role.ADMIN);
                SessionManager.getInstance().login(testUser);
                createAndShowMainFrame();
            } else {
                // Önce login ekranını göster
                if (!LoginDialog.showLoginDialog(null)) {
                    System.exit(0);
                    return;
                }
                createAndShowMainFrame();
            }
        });
    }

    private static void showCompanySetupWizard() {
        CompanySetupWizard wizard = new CompanySetupWizard(null);
        wizard.setVisible(true);

        // Kurulum tamamlandıysa login ekranına geç
        if (wizard.isSetupCompleted()) {
            if (!LoginDialog.showLoginDialog(null)) {
                System.exit(0);
                return;
            }
            createAndShowMainFrame();
        } else {
            // Kurulum tamamlanmadan çıkış yapıldı
            System.exit(0);
        }
    }

    private static void createAndShowMainFrame() {
        mainFrame = new JFrame("Stokbizde - Stok Yönetim Sistemi");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setLocationRelativeTo(null);

        // Modern üst araç çubuğu (JToolBar)
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(Color.WHITE);

        toolBar.add(createToolbarButton("Hızlı Ekran", loadSvgIcon("/com/stokbizde/ui/images/logo/dashboard.svg"),
            e -> showDashboard()));

        toolBar.add(createToolbarButton("Stok", loadSvgIcon("/com/stokbizde/ui/images/logo/inventory.svg"),
            e -> showPanel("Stok Yönetimi")));

        toolBar.add(createToolbarButton("Cari", loadSvgIcon("/com/stokbizde/ui/images/logo/customer.svg"),
            e -> showPanel("Cari Yönetimi")));

        toolBar.add(createToolbarButton("Fişler", loadSvgIcon("/com/stokbizde/ui/images/logo/receipt.svg"),
            e -> showPanel("Fişler")));

        JButton transferBtn = createToolbarButton("İşlemler", loadSvgIcon("/com/stokbizde/ui/images/logo/operation.svg"),
            e -> showTransferPanel());
        transferBtn.setEnabled(SessionManager.getInstance().canMakeTransfers());
        toolBar.add(transferBtn);

        JButton reportsBtn = createToolbarButton("Raporlar", loadSvgIcon("/com/stokbizde/ui/images/logo/report.svg"),
            e -> showPanel("Raporlar"));
        reportsBtn.setEnabled(SessionManager.getInstance().canViewReports());
        toolBar.add(reportsBtn);

        JButton settingsBtn = createToolbarButton("Tanımlar", loadSvgIcon("/com/stokbizde/ui/images/logo/gear.svg"), null);
        settingsBtn.addActionListener(e -> showSettingsMenu(settingsBtn));
        toolBar.add(settingsBtn);

        toolBar.addSeparator();

        toolBar.add(createToolbarButton("Çıkış", loadSvgIcon("/com/stokbizde/ui/images/logo/exit.svg"),
            e -> logout()));

        mainFrame.add(toolBar, BorderLayout.NORTH);

        // Orta panel: hoş geldiniz/logolu panel
        centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        showWelcomeScreen();
        mainFrame.add(centerPanel, BorderLayout.CENTER);

        // Alt status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        statusBar.setBackground(new Color(245, 245, 245));
        statusLabel = new JLabel("Hazır");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusBar.add(statusLabel, BorderLayout.WEST);

        String userInfo = "Kullanıcı: " + SessionManager.getInstance().getCurrentUserFullName() +
            " (" + SessionManager.getInstance().getCurrentUserRole() + ")";
        if (DEVELOPMENT_MODE) {
            userInfo += " [DEV MODE]";
        }
        JLabel userLabel = new JLabel(userInfo);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (DEVELOPMENT_MODE) {
            userLabel.setForeground(new Color(255, 100, 0));
        }
        statusBar.add(userLabel, BorderLayout.CENTER);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusBar.add(dateLabel, BorderLayout.EAST);
        mainFrame.add(statusBar, BorderLayout.SOUTH);

        mainFrame.setVisible(true);
    }

    private static void showWelcomeScreen() {
        centerPanel.removeAll();
        JLabel logoLabel = new JLabel("<html><div style='text-align:center;'>" +
            "<h1 style='font-size:40px;color:#444;'>STOKBİZDE</h1>" +
            "<div style='font-size:18px;color:#888;'>Stok Yönetim Sistemi</div>" +
            "<div style='font-size:14px;color:#aaa;margin-top:20px;'>Hoş geldiniz, " +
            SessionManager.getInstance().getCurrentUserFullName() + "</div>" +
            "</div></html>");
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(logoLabel);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private static void showDashboard() {
        updateStatus("Hızlı Ekran");
        showWelcomeScreen();
    }

    private static void showPanel(String panelName) {
        updateStatus(panelName);
        centerPanel.removeAll();
        JLabel label = new JLabel(panelName + " - Yakında eklenecek", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        centerPanel.add(label);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private static void showTransferPanel() {
        if (!SessionManager.getInstance().canMakeTransfers()) {
            JOptionPane.showMessageDialog(mainFrame, "Bu işlem için yetkiniz yok!",
                "Yetki Hatası", JOptionPane.WARNING_MESSAGE);
            return;
        }
        updateStatus("Transfer İşlemleri");
        centerPanel.removeAll();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new TransferManagementPanel(), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private static void showSettingsMenu(JButton button) {
        JPopupMenu menu = new JPopupMenu();
        menu.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Şirket Bilgileri
        JMenuItem companyInfoItem = new JMenuItem("Şirket Bilgileri");
        companyInfoItem.addActionListener(e -> showCompanyInfo());
        menu.add(companyInfoItem);

        menu.addSeparator();


        // Kasa Kartları
        JMenuItem cashRegisterItem = new JMenuItem("Kasa Kartları");
        cashRegisterItem.addActionListener(e -> showCashRegisterManagement());
        menu.add(cashRegisterItem);

        // Döviz Kurları
        JMenuItem currencyItem = new JMenuItem("Döviz Kurları");
        currencyItem.addActionListener(e -> showPanel("Döviz Kurları"));
        menu.add(currencyItem);

        menu.addSeparator();



        // Ürün Tanımları - Alt menü ile
        JMenu productMenu = new JMenu("Ürün Tanımları");



        JMenuItem brandItem = new JMenuItem("Ürün Marka Tanımları");
        brandItem.addActionListener(e -> showPanel("Ürün Marka Tanımları"));
        productMenu.add(brandItem);

        JMenuItem groupItem = new JMenuItem("Ürün Grup Tanımları");
        groupItem.addActionListener(e -> showPanel("Ürün Grup Tanımları"));
        productMenu.add(groupItem);

        JMenuItem typeItem = new JMenuItem("Ürün Çeşit Tanımları");
        typeItem.addActionListener(e -> showPanel("Ürün Çeşit Tanımları"));
        productMenu.add(typeItem);

        JMenuItem priceItem = new JMenuItem("Satış Fiyat Tanımları");
        priceItem.addActionListener(e -> showPanel("Satış Fiyat Tanımları"));
        productMenu.add(priceItem);

        JMenuItem categoryItem = new JMenuItem("Kategori Tanımları");
        categoryItem.addActionListener(e -> showPanel("Kategori Tanımları"));
        productMenu.add(categoryItem);

        JMenuItem sizeItem = new JMenuItem("Beden Tanımları");
        sizeItem.addActionListener(e -> showPanel("Beden Tanımları"));
        productMenu.add(sizeItem);

        JMenuItem colorItem = new JMenuItem("Renk Tanımları");
        colorItem.addActionListener(e -> showPanel("Renk Tanımları"));
        productMenu.add(colorItem);


        menu.add(productMenu);

        // Diğer Tanımlar
        JMenuItem otherDefinitionsItem = new JMenuItem("Diğer Tanımlar");
        otherDefinitionsItem.addActionListener(e -> showPanel("Diğer Tanımlar"));
        menu.add(otherDefinitionsItem);

        menu.addSeparator();

        // Aktarımlar
        JMenuItem transfersItem = new JMenuItem("Aktarımlar");
        transfersItem.addActionListener(e -> showPanel("Aktarımlar"));
        menu.add(transfersItem);

        // RS232 Cihazlar
        JMenuItem rs232Item = new JMenuItem("RS232 Cihazlar");
        rs232Item.addActionListener(e -> showPanel("RS232 Cihazlar"));
        menu.add(rs232Item);

        // Parametreler
        JMenuItem parametersItem = new JMenuItem("Parametreler");
        parametersItem.addActionListener(e -> showPanel("Parametreler"));
        menu.add(parametersItem);

        // Yardım
        JMenuItem helpItem = new JMenuItem("Yardım");
        helpItem.addActionListener(e -> showPanel("Yardım"));
        menu.add(helpItem);

        menu.addSeparator();

        // Şube & Depo Yönetimi
        if (SessionManager.getInstance().canManageBranches()) {
            JMenuItem locationItem = new JMenuItem("Şube & Depo Yönetimi");
            locationItem.addActionListener(e -> showLocationManagement());
            menu.add(locationItem);
        }

        // Kullanıcı Yönetimi
        if (SessionManager.getInstance().canManageUsers()) {
            JMenuItem userItem = new JMenuItem("Kullanıcı Yönetimi");
            userItem.addActionListener(e -> showUserManagement());
            menu.add(userItem);
        }

        menu.addSeparator();


        // Şifre Değiştir (herkes)
        JMenuItem passwordItem = new JMenuItem("Şifremi Değiştir");
        passwordItem.addActionListener(e -> showChangePasswordDialog());
        menu.add(passwordItem);

        if (menu.getComponentCount() > 0) {
            menu.show(button, 0, button.getHeight());
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Bu menüde erişebileceğiniz seçenek yok!",
                "Bilgi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void showBranchManagement() {
        if (!SessionManager.getInstance().canManageBranches()) {
            JOptionPane.showMessageDialog(mainFrame, "Bu işlem için yetkiniz yok!",
                "Yetki Hatası", JOptionPane.WARNING_MESSAGE);
            return;
        }
        updateStatus("Şube Yönetimi");
        centerPanel.removeAll();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new BranchManagementPanel(), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private static void showLocationManagement() {
        if (!SessionManager.getInstance().canManageBranches()) {
            JOptionPane.showMessageDialog(mainFrame, "Bu işlem için yetkiniz yok!",
                "Yetki Hatası", JOptionPane.WARNING_MESSAGE);
            return;
        }
        updateStatus("Şube & Depo Yönetimi");
        centerPanel.removeAll();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new LocationManagementPanel(), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private static void showCashRegisterManagement() {
        updateStatus("Kasa Yönetimi");
        centerPanel.removeAll();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new CashRegisterManagementPanel(), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private static void showUserManagement() {
        if (!SessionManager.getInstance().canManageUsers()) {
            JOptionPane.showMessageDialog(mainFrame, "Bu işlem için yetkiniz yok!",
                "Yetki Hatası", JOptionPane.WARNING_MESSAGE);
            return;
        }
        updateStatus("Kullanıcı Yönetimi");
        centerPanel.removeAll();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new UserManagementPanel(), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private static void showCompanyInfo() {
        updateStatus("Şirket Bilgileri");
        centerPanel.removeAll();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new CompanyInfoPanel(), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private static void showChangePasswordDialog() {
        JPasswordField currentPasswordField = new JPasswordField(20);
        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Mevcut Şifre:"));
        panel.add(currentPasswordField);
        panel.add(new JLabel("Yeni Şifre:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Yeni Şifre Tekrar:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Şifre Değiştir",

            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Yeni şifre boş olamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(mainFrame, "Yeni şifreler eşleşmiyor!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Şifre değiştirme işlemi burada yapılacak
            JOptionPane.showMessageDialog(mainFrame, "Şifre değiştirme özelliği aktif edilecek!",
                "Bilgi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void logout() {
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Çıkmak istediğinize emin misiniz?", "Çıkış", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().logout();
            mainFrame.dispose();

            // Yeniden login ekranını göster
            if (LoginDialog.showLoginDialog(null)) {
                createAndShowMainFrame();
            } else {
                System.exit(0);
            }
        }
    }

    private static void updateStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }

    // Modern toolbar butonu (ikon+metin)
    private static JButton createToolbarButton(String text, Icon icon) {
        return createToolbarButton(text, icon, null);
    }

    private static JButton createToolbarButton(String text, Icon icon, java.awt.event.ActionListener action) {
        JButton button = new JButton(text, icon);
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setIconTextGap(8);
        if (action != null) button.addActionListener(action);
        return button;
    }

    // SVG ikonunu yükleyen yardımcı fonksiyon
    private static Icon loadSvgIcon(String path) {
        try {
            SVGIcon icon = new SVGIcon();
            URL url = MainApp.class.getResource(path);
            icon.setSvgURI(url.toURI());
            icon.setScaleToFit(true);
            icon.setAntiAlias(true);
            icon.setPreferredSize(new java.awt.Dimension(18, 18));
            return icon;
        } catch (Exception e) {
            return null;
        }
    }
}

