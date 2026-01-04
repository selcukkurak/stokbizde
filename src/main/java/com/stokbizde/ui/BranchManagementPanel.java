package com.stokbizde.ui;

import com.stokbizde.dao.BranchDAO;
import com.stokbizde.dao.LocationDAO;
import com.stokbizde.model.Branch;
import com.stokbizde.model.Province;
import com.stokbizde.model.District;
import com.stokbizde.model.Neighborhood;
import com.stokbizde.util.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class BranchManagementPanel extends JPanel {
    private final BranchDAO branchDAO = new BranchDAO();
    private final LocationDAO locationDAO = new LocationDAO();
    private JTable branchTable;
    private JTextField nameField, searchField;
    private JComboBox<String> cityCombo, districtCombo, neighborhoodCombo;
    private DefaultTableModel tableModel;

    // Veritabanından çekilen il/ilçe/mahalle verileri
    private List<Province> provinces = new ArrayList<>();
    private Province selectedProvince = null;
    private District selectedDistrict = null;

    public BranchManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Konum verilerini başlat
        initLocationData();

        // Yetki kontrolü
        if (!SessionManager.getInstance().canManageBranches()) {
            setLayout(new BorderLayout());
            JLabel accessDenied = new JLabel("Bu sayfaya erişim yetkiniz yok!", SwingConstants.CENTER);
            accessDenied.setFont(new Font("Segoe UI", Font.BOLD, 18));
            accessDenied.setForeground(Color.RED);
            add(accessDenied, BorderLayout.CENTER);
            return;
        }

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Arama:"));
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Yeni Şube Ekle"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Şube Adı:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("İl:"), gbc);
        gbc.gridx = 1;
        cityCombo = new JComboBox<>();
        cityCombo.addItem("Seçiniz...");
        // Veritabanından illeri yükle
        for (Province province : provinces) {
            cityCombo.addItem(province.getProvinceName());
        }
        cityCombo.addActionListener(e -> updateDistrictCombo());
        formPanel.add(cityCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("İlçe:"), gbc);
        gbc.gridx = 1;
        districtCombo = new JComboBox<>();
        districtCombo.addItem("Seçiniz...");
        districtCombo.setEnabled(false);
        districtCombo.addActionListener(e -> updateNeighborhoodCombo());
        formPanel.add(districtCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Mahalle:"), gbc);
        gbc.gridx = 1;
        neighborhoodCombo = new JComboBox<>();
        neighborhoodCombo.addItem("Seçiniz...");
        neighborhoodCombo.setEnabled(false);
        formPanel.add(neighborhoodCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton addButton = new JButton("Ekle");
        addButton.addActionListener(e -> addBranch());
        formPanel.add(addButton, gbc);

        add(formPanel, BorderLayout.WEST);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Ad", "Konum"}, 0);
        branchTable = new JTable(tableModel);
        branchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        branchTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(branchTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Şubeler"));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton deleteButton = new JButton("Sil");
        deleteButton.addActionListener(e -> deleteBranch());
        buttonPanel.add(deleteButton);
        JButton refreshButton = new JButton("Yenile");
        refreshButton.addActionListener(e -> loadBranches());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadBranches();
    }

    private void addBranch() {
        try {
            String branchName = nameField.getText().trim();
            String city = (String) cityCombo.getSelectedItem();
            String district = (String) districtCombo.getSelectedItem();
            String neighborhoodWithPostal = (String) neighborhoodCombo.getSelectedItem();

            if (branchName.isEmpty() || city == null || city.equals("Seçiniz...") ||
                district == null || district.equals("Seçiniz...") ||
                neighborhoodWithPostal == null || neighborhoodWithPostal.equals("Seçiniz...")) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
                return;
            }

            // Mahalle adından posta kodunu ayır (eğer varsa)
            String neighborhood = neighborhoodWithPostal.replaceAll("\\s*\\(.*\\)\\s*$", "").trim();

            String location = city + " / " + district + " / " + neighborhood;
            Branch branch = new Branch(null, branchName, location);

            branchDAO.addBranch(branch);
            loadBranches();

            // Formu temizle
            nameField.setText("");
            cityCombo.setSelectedIndex(0);
            districtCombo.removeAllItems();
            districtCombo.addItem("Seçiniz...");
            districtCombo.setEnabled(false);
            neighborhoodCombo.removeAllItems();
            neighborhoodCombo.addItem("Seçiniz...");
            neighborhoodCombo.setEnabled(false);
            selectedProvince = null;
            selectedDistrict = null;

            JOptionPane.showMessageDialog(this, "Şube başarıyla eklendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBranch() {
        int selectedRow = branchTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                String id = (String) tableModel.getValueAt(selectedRow, 0);
                branchDAO.deleteBranch(id);
                loadBranches();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir şube seçin.");
        }
    }

    private void loadBranches() {
        try {
            tableModel.setRowCount(0);
            List<Branch> branches = branchDAO.getAllBranches();
            for (Branch b : branches) {
                tableModel.addRow(new Object[]{b.getId(), b.getName(), b.getLocation()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }

    private void filterTable() {
        String query = searchField.getText().toLowerCase();
        try {
            tableModel.setRowCount(0);
            List<Branch> branches = branchDAO.getAllBranches();
            for (Branch b : branches) {
                if (b.getName().toLowerCase().contains(query) || b.getLocation().toLowerCase().contains(query)) {
                    tableModel.addRow(new Object[]{b.getId(), b.getName(), b.getLocation()});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }

    private void initLocationData() {
        try {
            // Veritabanından tüm il verilerini çek
            provinces = locationDAO.getAllProvinces();

            if (provinces == null || provinces.isEmpty()) {
                System.out.println("UYARI: Veritabanında adres verisi bulunamadı!");
                JOptionPane.showMessageDialog(this,
                    "Adres verileri henüz yüklenmemiş.\nLütfen uygulamayı yeniden başlatın veya sistem yöneticisiyle iletişime geçin.",
                    "Veri Bulunamadı",
                    JOptionPane.WARNING_MESSAGE);
                provinces = new ArrayList<>();
            } else {
                System.out.println("✓ " + provinces.size() + " il verisi yüklendi.");
            }
        } catch (Exception e) {
            System.err.println("Adres verileri yüklenirken hata: " + e.getMessage());
            e.printStackTrace();
            provinces = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                "Adres verileri yüklenirken hata oluştu: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDistrictCombo() {
        String selectedCityName = (String) cityCombo.getSelectedItem();
        districtCombo.removeAllItems();
        districtCombo.addItem("Seçiniz...");
        neighborhoodCombo.removeAllItems();
        neighborhoodCombo.addItem("Seçiniz...");
        neighborhoodCombo.setEnabled(false);
        selectedProvince = null;
        selectedDistrict = null;

        if (selectedCityName != null && !selectedCityName.equals("Seçiniz...")) {
            // Seçilen ili bul
            for (Province province : provinces) {
                if (province.getProvinceName().equals(selectedCityName)) {
                    selectedProvince = province;
                    break;
                }
            }

            if (selectedProvince != null && selectedProvince.getDistricts() != null) {
                for (District district : selectedProvince.getDistricts()) {
                    districtCombo.addItem(district.getDistrictName());
                }
                districtCombo.setEnabled(true);
            } else {
                districtCombo.setEnabled(false);
            }
        } else {
            districtCombo.setEnabled(false);
        }
    }

    private void updateNeighborhoodCombo() {
        String selectedDistrictName = (String) districtCombo.getSelectedItem();
        neighborhoodCombo.removeAllItems();
        neighborhoodCombo.addItem("Seçiniz...");
        selectedDistrict = null;

        if (selectedDistrictName != null && !selectedDistrictName.equals("Seçiniz...") && selectedProvince != null) {
            // Seçilen ilçeyi bul
            for (District district : selectedProvince.getDistricts()) {
                if (district.getDistrictName().equals(selectedDistrictName)) {
                    selectedDistrict = district;
                    break;
                }
            }

            if (selectedDistrict != null && selectedDistrict.getNeighborhoods() != null) {
                for (Neighborhood neighborhood : selectedDistrict.getNeighborhoods()) {
                    String displayText = neighborhood.getNeighborhoodName();
                    // Posta kodu varsa göster
                    if (neighborhood.getPostalCode() != null && !neighborhood.getPostalCode().isEmpty()) {
                        displayText += " (" + neighborhood.getPostalCode() + ")";
                    }
                    neighborhoodCombo.addItem(displayText);
                }
                neighborhoodCombo.setEnabled(true);
            } else {
                neighborhoodCombo.setEnabled(false);
            }
        } else {
            neighborhoodCombo.setEnabled(false);
        }
    }
}
