package com.stokbizde.ui;

import com.stokbizde.dao.CompanyDAO;
import com.stokbizde.model.Company;
import com.stokbizde.util.SessionManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class CompanyInfoPanel extends JPanel {
    private CompanyDAO companyDAO;
    private Company company;
    private boolean isAdmin;

    // Form alanları
    private JTextField nameField;
    private JTextField activeCompanyField;
    private JTextField activeWarehouseField;
    private JTextArea addressArea;
    private JTextField districtField;
    private JTextField cityField;
    private JTextField phone1Field;
    private JTextField phone2Field;
    private JTextField emailField;
    private JTextField websiteField;
    private JLabel logoLabel;
    private JButton selectLogoButton;
    private JButton saveButton;

    private byte[] currentLogoData;

    public CompanyInfoPanel() {
        this.companyDAO = new CompanyDAO();
        this.company = companyDAO.getCompanyInfo();
        this.isAdmin = SessionManager.getInstance().getCurrentUserRole().equals("ADMIN");

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadCompanyData();
    }

    private void initComponents() {
        // Ana container
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Başlık
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // İçerik paneli
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(Color.WHITE);

        // Sol: Form alanları
        JPanel formPanel = createFormPanel();
        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Sağ: Logo alanı
        JPanel logoPanel = createLogoPanel();
        contentPanel.add(logoPanel, BorderLayout.EAST);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Alt: Kaydet butonu
        if (isAdmin) {
            JPanel buttonPanel = createButtonPanel();
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Şirket Bilgileri");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        if (!isAdmin) {
            JLabel warningLabel = new JLabel("Sadece görüntüleme modu (Yalnızca Admin değişiklik yapabilir)");
            warningLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            warningLabel.setForeground(new Color(255, 200, 100));
            panel.add(warningLabel, BorderLayout.EAST);
        }

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Genel Bilgiler
        panel.add(createSectionPanel("Genel Bilgiler", new Component[]{
            createFieldRow("Mağaza Adı:", nameField = createTextField()),
            createFieldRow("Aktif Şirket:", activeCompanyField = createTextField()),
            createFieldRow("Aktif Depo:", activeWarehouseField = createTextField())
        }));

        panel.add(Box.createVerticalStrut(15));

        // Adres Bilgileri
        JPanel addressPanel = new JPanel(new BorderLayout(5, 5));
        addressPanel.setBackground(Color.WHITE);
        JLabel addressLabel = new JLabel("Adres:");
        addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressArea = new JTextArea(3, 30);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        addressArea.setEnabled(isAdmin);
        JScrollPane scrollPane = new JScrollPane(addressArea);
        scrollPane.setPreferredSize(new Dimension(400, 80));
        addressPanel.add(addressLabel, BorderLayout.NORTH);
        addressPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(createSectionPanel("Adres Bilgileri", new Component[]{
            addressPanel,
            createFieldRow("Semt:", districtField = createTextField()),
            createFieldRow("Şehir:", cityField = createTextField())
        }));

        panel.add(Box.createVerticalStrut(15));

        // İletişim Bilgileri
        panel.add(createSectionPanel("İletişim Bilgileri", new Component[]{
            createFieldRow("Telefon 1:", phone1Field = createTextField()),
            createFieldRow("Telefon 2:", phone2Field = createTextField()),
            createFieldRow("E-Posta:", emailField = createTextField()),
            createFieldRow("Web:", websiteField = createTextField())
        }));

        return panel;
    }

    private JPanel createLogoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(300, 400));

        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Şirket Logosu"
        );
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));

        // Logo gösterim alanı
        logoLabel = new JLabel("Logo yok", SwingConstants.CENTER);
        logoLabel.setPreferredSize(new Dimension(280, 280));
        logoLabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2));
        logoLabel.setBackground(new Color(250, 250, 250));
        logoLabel.setOpaque(true);
        panel.add(logoLabel, BorderLayout.CENTER);

        // Logo seçim butonu
        if (isAdmin) {
            selectLogoButton = new JButton("Logo Seç");
            selectLogoButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            selectLogoButton.setBackground(new Color(52, 152, 219));
            selectLogoButton.setForeground(Color.WHITE);
            selectLogoButton.setFocusPainted(false);
            selectLogoButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            selectLogoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            selectLogoButton.addActionListener(e -> selectLogo());
            panel.add(selectLogoButton, BorderLayout.SOUTH);
        }

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        saveButton = new JButton("Kaydet");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> saveCompanyInfo());
        panel.add(saveButton);

        return panel;
    }

    private JPanel createSectionPanel(String title, Component[] components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            title
        );
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));

        for (Component comp : components) {
            panel.add(comp);
            panel.add(Box.createVerticalStrut(10));
        }

        return panel;
    }

    private JPanel createFieldRow(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(120, 30));
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setEnabled(isAdmin);
        return field;
    }

    private void loadCompanyData() {
        if (company != null) {
            nameField.setText(company.getName() != null ? company.getName() : "");
            activeCompanyField.setText(company.getActiveCompany() != null ? company.getActiveCompany() : "");
            activeWarehouseField.setText(company.getActiveWarehouse() != null ? company.getActiveWarehouse() : "");
            addressArea.setText(company.getAddress() != null ? company.getAddress() : "");
            districtField.setText(company.getDistrict() != null ? company.getDistrict() : "");
            cityField.setText(company.getCity() != null ? company.getCity() : "");
            phone1Field.setText(company.getPhone1() != null ? company.getPhone1() : "");
            phone2Field.setText(company.getPhone2() != null ? company.getPhone2() : "");
            emailField.setText(company.getEmail() != null ? company.getEmail() : "");
            websiteField.setText(company.getWebsite() != null ? company.getWebsite() : "");

            // Logo yükle
            if (company.getLogoImage() != null) {
                currentLogoData = company.getLogoImage();
                displayLogo(currentLogoData);
            }
        }
    }

    private void selectLogo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Resim Dosyaları", "jpg", "jpeg", "png", "gif", "bmp"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    // Resmi byte array'e çevir
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    String extension = getFileExtension(file.getName());
                    ImageIO.write(img, extension.isEmpty() ? "png" : extension, baos);
                    currentLogoData = baos.toByteArray();

                    // Göster
                    displayLogo(currentLogoData);

                    JOptionPane.showMessageDialog(this,
                        "Logo seçildi. Değişikliklerin kaydedilmesi için 'Kaydet' butonuna tıklayın.",
                        "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Logo yüklenirken hata oluştu: " + ex.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayLogo(byte[] imageData) {
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
            if (img != null) {
                // Resmi ölçeklendir
                Image scaled = img.getScaledInstance(280, 280, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaled));
                logoLabel.setText("");
            }
        } catch (Exception ex) {
            logoLabel.setIcon(null);
            logoLabel.setText("Logo yüklenemedi");
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }

    private void saveCompanyInfo() {
        // Validasyon
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Mağaza adı boş bırakılamaz!",
                "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Değerleri kaydet
        company.setName(nameField.getText().trim());
        company.setActiveCompany(activeCompanyField.getText().trim());
        company.setActiveWarehouse(activeWarehouseField.getText().trim());
        company.setAddress(addressArea.getText().trim());
        company.setDistrict(districtField.getText().trim());
        company.setCity(cityField.getText().trim());
        company.setPhone1(phone1Field.getText().trim());
        company.setPhone2(phone2Field.getText().trim());
        company.setEmail(emailField.getText().trim());
        company.setWebsite(websiteField.getText().trim());

        // Logo kaydet
        if (currentLogoData != null) {
            company.setLogoImage(currentLogoData);
        }

        // İlk kurulum değilse initialized'ı true yap
        if (!company.isInitialized()) {
            company.setInitialized(true);
        }

        companyDAO.updateCompanyInfo(company);

        JOptionPane.showMessageDialog(this,
            "Şirket bilgileri başarıyla kaydedildi!",
            "Başarılı", JOptionPane.INFORMATION_MESSAGE);
    }
}

