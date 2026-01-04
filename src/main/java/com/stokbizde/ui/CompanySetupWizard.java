package com.stokbizde.ui;

import com.stokbizde.dao.CompanyDAO;
import com.stokbizde.model.Company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class CompanySetupWizard extends JDialog {
    private CompanyDAO companyDAO;
    private Company company;

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
    private byte[] currentLogoData;

    private boolean setupCompleted = false;

    public CompanySetupWizard(Frame parent) {
        super(parent, "İlk Kurulum - Şirket Bilgileri", true);
        this.companyDAO = new CompanyDAO();
        this.company = new Company();

        setSize(900, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Başlık paneli
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Ana içerik
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Sol: Form alanları
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Sağ: Logo alanı
        JPanel logoPanel = createLogoPanel();
        mainPanel.add(logoPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        // Alt panel: Butonlar
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(41, 128, 185));

        JLabel titleLabel = new JLabel("Hoş Geldiniz!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        textPanel.add(titleLabel);

        textPanel.add(Box.createVerticalStrut(10));

        JLabel subtitleLabel = new JLabel("Uygulamayı kullanmaya başlamadan önce şirket bilgilerinizi giriniz.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        textPanel.add(subtitleLabel);

        JLabel noteLabel = new JLabel("Not: Bu bilgiler sadece yönetici tarafından güncellenebilecektir.");
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        noteLabel.setForeground(new Color(255, 200, 100));
        textPanel.add(noteLabel);

        panel.add(textPanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Genel Bilgiler
        panel.add(createSectionPanel("Genel Bilgiler", new Component[]{
            createFieldRow("Mağaza Adı: *", nameField = createTextField()),
            createFieldRow("Aktif Şirket:", activeCompanyField = createTextField())
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
        JScrollPane addrScrollPane = new JScrollPane(addressArea);
        addrScrollPane.setPreferredSize(new Dimension(400, 80));
        addressPanel.add(addressLabel, BorderLayout.NORTH);
        addressPanel.add(addrScrollPane, BorderLayout.CENTER);

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

        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    private JPanel createLogoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(300, 400));

        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Şirket Logosu (Opsiyonel)"
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
        JButton selectLogoButton = new JButton("Logo Seç");
        selectLogoButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectLogoButton.setBackground(new Color(52, 152, 219));
        selectLogoButton.setForeground(Color.WHITE);
        selectLogoButton.setFocusPainted(false);
        selectLogoButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        selectLogoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectLogoButton.addActionListener(e -> selectLogo());
        panel.add(selectLogoButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 20, 20));

        JButton cancelButton = new JButton("Çıkış");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "Kurulum tamamlanmadan uygulamayı kullanamazsınız.\nÇıkmak istediğinizden emin misiniz?",
                "Kurulum İptali", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        panel.add(cancelButton);

        JButton saveButton = new JButton("Kurulumu Tamamla");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> completeSetup());
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
        return field;
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

    private void completeSetup() {
        // Validasyon
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Mağaza adı boş bırakılamaz!",
                "Uyarı", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }

        // Değerleri kaydet
        company.setName(nameField.getText().trim());
        company.setActiveCompany(activeCompanyField.getText().trim());
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

        // İlk kurulum tamamlandı olarak işaretle
        company.setInitialized(true);

        try {
            companyDAO.updateCompanyInfo(company);
            setupCompleted = true;

            JOptionPane.showMessageDialog(this,
                "Şirket bilgileri başarıyla kaydedildi!\nŞimdi giriş yapabilirsiniz.",
                "Kurulum Tamamlandı", JOptionPane.INFORMATION_MESSAGE);

            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Bilgiler kaydedilirken hata oluştu: " + ex.getMessage(),
                "Hata", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isSetupCompleted() {
        return setupCompleted;
    }
}

