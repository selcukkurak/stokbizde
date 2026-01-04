package com.stokbizde.ui;

import com.stokbizde.dao.BranchDAO;
import com.stokbizde.dao.LocationDAO;
import com.stokbizde.dao.UserDAO;
import com.stokbizde.dao.WarehouseDAO;
import com.stokbizde.model.Branch;
import com.stokbizde.model.District;
import com.stokbizde.model.Neighborhood;
import com.stokbizde.model.Province;
import com.stokbizde.model.User;
import com.stokbizde.model.Warehouse;
import com.stokbizde.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class LocationManagementPanel extends JPanel {
    private final BranchDAO branchDAO;
    private final WarehouseDAO warehouseDAO;
    private final UserDAO userDAO;
    private final LocationDAO locationDAO;

    private JTabbedPane tabbedPane;
    private JTable branchTable;
    private JTable warehouseTable;
    private DefaultTableModel branchTableModel;
    private DefaultTableModel warehouseTableModel;

    public LocationManagementPanel() {
        this.branchDAO = new BranchDAO();
        this.warehouseDAO = new WarehouseDAO();
        this.userDAO = new UserDAO();
        this.locationDAO = new LocationDAO();

        setLayout(new BorderLayout());

        setBackground(Color.WHITE);

        if (!SessionManager.getInstance().canManageBranches()) {
            showAccessDenied();
            return;
        }

        initComponents();
        loadData();
    }

    private void showAccessDenied() {
        setLayout(new BorderLayout());
        JLabel accessDenied = new JLabel("Bu sayfaya erişim yetkiniz yok!", SwingConstants.CENTER);
        accessDenied.setFont(new Font("Segoe UI", Font.BOLD, 18));
        accessDenied.setForeground(new Color(231, 76, 60));
        add(accessDenied, BorderLayout.CENTER);
    }

    private void initComponents() {
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);

        JPanel branchPanel = createBranchPanel();
        tabbedPane.addTab("Şubeler", branchPanel);

        JPanel warehousePanel = createWarehousePanel();
        tabbedPane.addTab("Depolar", warehousePanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Şube & Depo Yönetimi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createBranchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createStyledButton("Yeni Şube Ekle", new Color(46, 204, 113));
        addButton.addActionListener(e -> addBranch());
        buttonPanel.add(addButton);

        JButton editButton = createStyledButton("Düzenle", new Color(52, 152, 219));
        editButton.addActionListener(e -> editBranch());
        buttonPanel.add(editButton);

        JButton deleteButton = createStyledButton("Sil", new Color(231, 76, 60));
        deleteButton.addActionListener(e -> deleteBranch());
        buttonPanel.add(deleteButton);

        JButton toggleStatusButton = createStyledButton("Aktif/Pasif", new Color(243, 156, 18));
        toggleStatusButton.addActionListener(e -> toggleBranchStatus());
        buttonPanel.add(toggleStatusButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Şube Adı", "Şube Müdürü", "İl", "İlçe", "Mahalle", "Telefon 1", "Durum"};
        branchTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        branchTable = new JTable(branchTableModel);
        styleTable(branchTable);

        branchTable.getColumnModel().getColumn(0).setMinWidth(0);
        branchTable.getColumnModel().getColumn(0).setMaxWidth(0);
        branchTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(branchTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Şube İşlemleri
    private void addBranch() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Yeni Şube Ekle", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);

        JTextField addressField = new JTextField(20);
        JTextField phone1Field = new JTextField(20);
        JTextField phone2Field = new JTextField(20);

        // İl, İlçe, Mahalle ComboBox'ları
        JComboBox<String> provinceCombo = new JComboBox<>();
        provinceCombo.addItem("İl Seçiniz");
        JComboBox<String> districtCombo = new JComboBox<>();
        districtCombo.addItem("İlçe Seçiniz");
        districtCombo.setEnabled(false);
        JComboBox<String> neighborhoodCombo = new JComboBox<>();
        neighborhoodCombo.addItem("Mahalle Seçiniz");
        neighborhoodCombo.setEnabled(false);

        // İlleri yükle
        List<Province> provinces = locationDAO.getAllProvinces();
        for (Province province : provinces) {
            provinceCombo.addItem(province.getProvinceName());
        }

        // İl değiştiğinde ilçeleri yükle
        provinceCombo.addActionListener(e -> {
            districtCombo.removeAllItems();
            districtCombo.addItem("İlçe Seçiniz");
            neighborhoodCombo.removeAllItems();
            neighborhoodCombo.addItem("Mahalle Seçiniz");
            neighborhoodCombo.setEnabled(false);

            int selectedIndex = provinceCombo.getSelectedIndex();
            if (selectedIndex > 0) {
                Province selectedProvince = provinces.get(selectedIndex - 1);
                if (selectedProvince.getDistricts() != null) {
                    for (District district : selectedProvince.getDistricts()) {
                        districtCombo.addItem(district.getDistrictName());
                    }
                    districtCombo.setEnabled(true);
                }
            } else {
                districtCombo.setEnabled(false);
            }
        });

        // İlçe değiştiğinde mahalleleri yükle
        districtCombo.addActionListener(e -> {
            neighborhoodCombo.removeAllItems();
            neighborhoodCombo.addItem("Mahalle Seçiniz");

            int provinceIndex = provinceCombo.getSelectedIndex();
            int districtIndex = districtCombo.getSelectedIndex();

            if (provinceIndex > 0 && districtIndex > 0) {
                Province selectedProvince = provinces.get(provinceIndex - 1);
                if (selectedProvince.getDistricts() != null) {
                    District selectedDistrict = selectedProvince.getDistricts().get(districtIndex - 1);
                    if (selectedDistrict.getNeighborhoods() != null) {
                        for (Neighborhood neighborhood : selectedDistrict.getNeighborhoods()) {
                            neighborhoodCombo.addItem(neighborhood.getNeighborhoodName());
                        }
                        neighborhoodCombo.setEnabled(true);
                    }
                }
            } else {
                neighborhoodCombo.setEnabled(false);
            }
        });

        JComboBox<String> managerCombo = new JComboBox<>();
        managerCombo.addItem("Atanmamış");
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            managerCombo.addItem(user.getFullName());
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Şube Adı: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Şube Müdürü:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(managerCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("İl:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(provinceCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("İlçe:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(districtCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Mahalle:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(neighborhoodCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Adres:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(addressField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Telefon 1:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(phone1Field, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Telefon 2:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(phone2Field, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = createStyledButton("İptal", new Color(149, 165, 166));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        JButton saveButton = createStyledButton("Kaydet", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Şube adı boş olamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Telefon numarası kontrolü
            String phone1 = phone1Field.getText().trim();
            String phone2 = phone2Field.getText().trim();

            if (!phone1.isEmpty() && !isValidPhoneNumber(phone1)) {
                JOptionPane.showMessageDialog(dialog,
                    "Telefon 1 geçerli bir format değil!\nÖrnek: 05XX XXX XX XX",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!phone2.isEmpty() && !isValidPhoneNumber(phone2)) {
                JOptionPane.showMessageDialog(dialog,
                    "Telefon 2 geçerli bir format değil!\nÖrnek: 05XX XXX XX XX",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Branch branch = new Branch();
            branch.setName(nameField.getText().trim());

            // İl, İlçe, Mahalle bilgilerini kaydet
            int provinceIndex = provinceCombo.getSelectedIndex();
            if (provinceIndex > 0) {
                branch.setProvince((String) provinceCombo.getSelectedItem());
            }

            int districtIndex = districtCombo.getSelectedIndex();
            if (districtIndex > 0) {
                branch.setDistrict((String) districtCombo.getSelectedItem());
            }

            int neighborhoodIndex = neighborhoodCombo.getSelectedIndex();
            if (neighborhoodIndex > 0) {
                branch.setNeighborhood((String) neighborhoodCombo.getSelectedItem());
            }

            branch.setAddress(addressField.getText().trim());
            branch.setPhone1(formatPhoneNumber(phone1));
            branch.setPhone2(formatPhoneNumber(phone2));
            branch.setActive(true);

            int selectedIndex = managerCombo.getSelectedIndex();
            if (selectedIndex > 0) {
                User selectedUser = users.get(selectedIndex - 1);
                branch.setManagerId(selectedUser.getId());
                branch.setManagerName(selectedUser.getFullName());
            }

            branchDAO.insertBranch(branch);
            loadBranches();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Şube başarıyla eklendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void editBranch() {
        int selectedRow = branchTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen düzenlenecek şubeyi seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String branchId = (String) branchTableModel.getValueAt(selectedRow, 0);
        Branch branch = branchDAO.getBranchById(branchId);
        if (branch == null) {
            JOptionPane.showMessageDialog(this, "Şube bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Şube Düzenle", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(branch.getName(), 20);
        JTextField addressField = new JTextField(branch.getAddress(), 20);
        JTextField phone1Field = new JTextField(branch.getPhone1(), 20);
        JTextField phone2Field = new JTextField(branch.getPhone2(), 20);

        // İl, İlçe, Mahalle ComboBox'ları
        JComboBox<String> provinceCombo = new JComboBox<>();
        provinceCombo.addItem("İl Seçiniz");
        JComboBox<String> districtCombo = new JComboBox<>();
        districtCombo.addItem("İlçe Seçiniz");
        districtCombo.setEnabled(false);
        JComboBox<String> neighborhoodCombo = new JComboBox<>();
        neighborhoodCombo.addItem("Mahalle Seçiniz");
        neighborhoodCombo.setEnabled(false);

        // İlleri yükle ve mevcut ili seç
        List<Province> provinces = locationDAO.getAllProvinces();
        int selectedProvinceIndex = 0;
        for (int i = 0; i < provinces.size(); i++) {
            Province province = provinces.get(i);
            provinceCombo.addItem(province.getProvinceName());
            if (branch.getProvince() != null && branch.getProvince().equals(province.getProvinceName())) {
                selectedProvinceIndex = i + 1;
            }
        }

        // İl değiştiğinde ilçeleri yükle
        provinceCombo.addActionListener(e -> {
            districtCombo.removeAllItems();
            districtCombo.addItem("İlçe Seçiniz");
            neighborhoodCombo.removeAllItems();
            neighborhoodCombo.addItem("Mahalle Seçiniz");
            neighborhoodCombo.setEnabled(false);

            int selectedIndex = provinceCombo.getSelectedIndex();
            if (selectedIndex > 0) {
                Province selectedProvince = provinces.get(selectedIndex - 1);
                if (selectedProvince.getDistricts() != null) {
                    for (District district : selectedProvince.getDistricts()) {
                        districtCombo.addItem(district.getDistrictName());
                    }
                    districtCombo.setEnabled(true);
                }
            } else {
                districtCombo.setEnabled(false);
            }
        });

        // İlçe değiştiğinde mahalleleri yükle
        districtCombo.addActionListener(e -> {
            neighborhoodCombo.removeAllItems();
            neighborhoodCombo.addItem("Mahalle Seçiniz");

            int provinceIndex = provinceCombo.getSelectedIndex();
            int districtIndex = districtCombo.getSelectedIndex();

            if (provinceIndex > 0 && districtIndex > 0) {
                Province selectedProvince = provinces.get(provinceIndex - 1);
                if (selectedProvince.getDistricts() != null) {
                    District selectedDistrict = selectedProvince.getDistricts().get(districtIndex - 1);
                    if (selectedDistrict.getNeighborhoods() != null) {
                        for (Neighborhood neighborhood : selectedDistrict.getNeighborhoods()) {
                            neighborhoodCombo.addItem(neighborhood.getNeighborhoodName());
                        }
                        neighborhoodCombo.setEnabled(true);
                    }
                }
            } else {
                neighborhoodCombo.setEnabled(false);
            }
        });

        // Mevcut ili seç
        provinceCombo.setSelectedIndex(selectedProvinceIndex);

        // Mevcut ilçeyi seç
        if (selectedProvinceIndex > 0 && branch.getDistrict() != null) {
            Province selectedProvince = provinces.get(selectedProvinceIndex - 1);
            if (selectedProvince.getDistricts() != null) {
                for (int i = 0; i < selectedProvince.getDistricts().size(); i++) {
                    if (selectedProvince.getDistricts().get(i).getDistrictName().equals(branch.getDistrict())) {
                        districtCombo.setSelectedIndex(i + 1);
                        break;
                    }
                }
            }
        }

        // Mevcut mahalleyi seç
        if (selectedProvinceIndex > 0 && branch.getNeighborhood() != null) {
            int districtIndex = districtCombo.getSelectedIndex();
            if (districtIndex > 0) {
                Province selectedProvince = provinces.get(selectedProvinceIndex - 1);
                if (selectedProvince.getDistricts() != null) {
                    District selectedDistrict = selectedProvince.getDistricts().get(districtIndex - 1);
                    if (selectedDistrict.getNeighborhoods() != null) {
                        for (int i = 0; i < selectedDistrict.getNeighborhoods().size(); i++) {
                            if (selectedDistrict.getNeighborhoods().get(i).getNeighborhoodName().equals(branch.getNeighborhood())) {
                                neighborhoodCombo.setSelectedIndex(i + 1);
                                break;
                            }
                        }
                    }
                }
            }
        }

        JComboBox<String> managerCombo = new JComboBox<>();
        managerCombo.addItem("Atanmamış");
        List<User> users = userDAO.getAllUsers();
        int selectedManagerIndex = 0;
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            managerCombo.addItem(user.getFullName());
            if (branch.getManagerId() != null && branch.getManagerId().equals(user.getId())) {
                selectedManagerIndex = i + 1;
            }
        }
        managerCombo.setSelectedIndex(selectedManagerIndex);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Şube Adı: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Şube Müdürü:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(managerCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("İl:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(provinceCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("İlçe:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(districtCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Mahalle:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(neighborhoodCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Adres:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(addressField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Telefon 1:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(phone1Field, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Telefon 2:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(phone2Field, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = createStyledButton("İptal", new Color(149, 165, 166));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        JButton saveButton = createStyledButton("Kaydet", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Şube adı boş olamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Telefon numarası kontrolü
            String phone1 = phone1Field.getText().trim();
            String phone2 = phone2Field.getText().trim();

            if (!phone1.isEmpty() && !isValidPhoneNumber(phone1)) {
                JOptionPane.showMessageDialog(dialog,
                    "Telefon 1 geçerli bir format değil!\nÖrnek: 05XX XXX XX XX",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!phone2.isEmpty() && !isValidPhoneNumber(phone2)) {
                JOptionPane.showMessageDialog(dialog,
                    "Telefon 2 geçerli bir format değil!\nÖrnek: 05XX XXX XX XX",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            branch.setName(nameField.getText().trim());

            // İl, İlçe, Mahalle bilgilerini kaydet
            int provinceIndex = provinceCombo.getSelectedIndex();
            if (provinceIndex > 0) {
                branch.setProvince((String) provinceCombo.getSelectedItem());
            } else {
                branch.setProvince(null);
            }

            int districtIndex = districtCombo.getSelectedIndex();
            if (districtIndex > 0) {
                branch.setDistrict((String) districtCombo.getSelectedItem());
            } else {
                branch.setDistrict(null);
            }

            int neighborhoodIndex = neighborhoodCombo.getSelectedIndex();
            if (neighborhoodIndex > 0) {
                branch.setNeighborhood((String) neighborhoodCombo.getSelectedItem());
            } else {
                branch.setNeighborhood(null);
            }

            branch.setAddress(addressField.getText().trim());
            branch.setPhone1(formatPhoneNumber(phone1));
            branch.setPhone2(formatPhoneNumber(phone2));

            int selectedIndex = managerCombo.getSelectedIndex();
            if (selectedIndex > 0) {
                User selectedUser = users.get(selectedIndex - 1);
                branch.setManagerId(selectedUser.getId());
                branch.setManagerName(selectedUser.getFullName());
            } else {
                branch.setManagerId(null);
                branch.setManagerName(null);
            }

            branchDAO.updateBranch(branch);
            loadBranches();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Şube başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void deleteBranch() {
        int selectedRow = branchTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek şubeyi seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
            "Şubeyi silmek istediğinizden emin misiniz?",
            "Onay", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            String branchId = (String) branchTableModel.getValueAt(selectedRow, 0);
            branchDAO.deleteBranch(branchId);
            loadBranches();
            JOptionPane.showMessageDialog(this, "Şube başarıyla silindi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void toggleBranchStatus() {
        int selectedRow = branchTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen durumunu değiştirmek istediğiniz şubeyi seçin!",
                "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String branchId = (String) branchTableModel.getValueAt(selectedRow, 0);
        Branch branch = branchDAO.getBranchById(branchId);
        if (branch == null) {
            JOptionPane.showMessageDialog(this, "Şube bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentStatus = branch.isActive() ? "Aktif" : "Pasif";
        String newStatus = branch.isActive() ? "Pasif" : "Aktif";

        int result = JOptionPane.showConfirmDialog(this,
            "Şube durumunu '" + currentStatus + "' den '" + newStatus + "' e değiştirmek istiyor musunuz?",
            "Onay", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            branch.setActive(!branch.isActive());
            branchDAO.updateBranch(branch);
            loadBranches();
            JOptionPane.showMessageDialog(this,
                "Şube durumu başarıyla '" + newStatus + "' olarak değiştirildi!",
                "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Depo İşlemleri
    private void addWarehouse() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Yeni Depo Ekle", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField cityField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField phone1Field = new JTextField(20);
        JTextField phone2Field = new JTextField(20);
        JComboBox<String> responsibleCombo = new JComboBox<>();
        responsibleCombo.addItem("Atanmamış");
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            responsibleCombo.addItem(user.getFullName());
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Depo Adı: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Depo Sorumlusu:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(responsibleCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Şehir:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(cityField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Adres:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(addressField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Telefon 1:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(phone1Field, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Telefon 2:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(phone2Field, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = createStyledButton("İptal", new Color(149, 165, 166));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        JButton saveButton = createStyledButton("Kaydet", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Depo adı boş olamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Telefon numarası kontrolü
            String phone1 = phone1Field.getText().trim();
            String phone2 = phone2Field.getText().trim();

            if (!phone1.isEmpty() && !isValidPhoneNumber(phone1)) {
                JOptionPane.showMessageDialog(dialog,
                    "Telefon 1 geçerli bir format değil!\nÖrnek: 05XX XXX XX XX",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!phone2.isEmpty() && !isValidPhoneNumber(phone2)) {
                JOptionPane.showMessageDialog(dialog,
                    "Telefon 2 geçerli bir format değil!\nÖrnek: 05XX XXX XX XX",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Warehouse warehouse = new Warehouse();
            warehouse.setName(nameField.getText().trim());
            warehouse.setCity(cityField.getText().trim());
            warehouse.setAddress(addressField.getText().trim());
            warehouse.setPhone1(formatPhoneNumber(phone1));
            warehouse.setPhone2(formatPhoneNumber(phone2));
            warehouse.setActive(true);

            int selectedIndex = responsibleCombo.getSelectedIndex();
            if (selectedIndex > 0) {
                User selectedUser = users.get(selectedIndex - 1);
                warehouse.setResponsibleId(selectedUser.getId());
                warehouse.setResponsibleName(selectedUser.getFullName());
            }

            warehouseDAO.insertWarehouse(warehouse);
            loadWarehouses();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Depo başarıyla eklendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void editWarehouse() {
        int selectedRow = warehouseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen düzenlenecek depoyu seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String warehouseId = (String) warehouseTableModel.getValueAt(selectedRow, 0);
        Warehouse warehouse = warehouseDAO.getWarehouseById(warehouseId);
        if (warehouse == null) {
            JOptionPane.showMessageDialog(this, "Depo bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Depo Düzenle", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(warehouse.getName(), 20);
        JTextField cityField = new JTextField(warehouse.getCity(), 20);
        JTextField addressField = new JTextField(warehouse.getAddress(), 20);
        JTextField phone1Field = new JTextField(warehouse.getPhone1(), 20);
        JTextField phone2Field = new JTextField(warehouse.getPhone2(), 20);
        JComboBox<String> responsibleCombo = new JComboBox<>();
        responsibleCombo.addItem("Atanmamış");
        List<User> users = userDAO.getAllUsers();
        int selectedResponsibleIndex = 0;
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            responsibleCombo.addItem(user.getFullName());
            if (warehouse.getResponsibleId() != null && warehouse.getResponsibleId().equals(user.getId())) {
                selectedResponsibleIndex = i + 1;
            }
        }
        responsibleCombo.setSelectedIndex(selectedResponsibleIndex);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Depo Adı: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Depo Sorumlusu:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(responsibleCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Şehir:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(cityField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Adres:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(addressField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Telefon 1:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(phone1Field, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Telefon 2:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(phone2Field, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = createStyledButton("İptal", new Color(149, 165, 166));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        JButton saveButton = createStyledButton("Kaydet", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Depo adı boş olamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Telefon numarası kontrolü
            String phone1 = phone1Field.getText().trim();
            String phone2 = phone2Field.getText().trim();

            if (!phone1.isEmpty() && !isValidPhoneNumber(phone1)) {
                JOptionPane.showMessageDialog(dialog,
                    "Telefon 1 geçerli bir format değil!\nÖrnek: 05XX XXX XX XX",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!phone2.isEmpty() && !isValidPhoneNumber(phone2)) {
                JOptionPane.showMessageDialog(dialog,
                    "Telefon 2 geçerli bir format değil!\nÖrnek: 05XX XXX XX XX",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            warehouse.setName(nameField.getText().trim());
            warehouse.setCity(cityField.getText().trim());
            warehouse.setAddress(addressField.getText().trim());
            warehouse.setPhone1(formatPhoneNumber(phone1));
            warehouse.setPhone2(formatPhoneNumber(phone2));

            int selectedIndex = responsibleCombo.getSelectedIndex();
            if (selectedIndex > 0) {
                User selectedUser = users.get(selectedIndex - 1);
                warehouse.setResponsibleId(selectedUser.getId());
                warehouse.setResponsibleName(selectedUser.getFullName());
            } else {
                warehouse.setResponsibleId(null);
                warehouse.setResponsibleName(null);
            }

            warehouseDAO.updateWarehouse(warehouse);
            loadWarehouses();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Depo başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void deleteWarehouse() {
        int selectedRow = warehouseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek depoyu seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
            "Depoyu silmek istediğinizden emin misiniz?",
            "Onay", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            String warehouseId = (String) warehouseTableModel.getValueAt(selectedRow, 0);
            warehouseDAO.deleteWarehouse(warehouseId);
            loadWarehouses();
            JOptionPane.showMessageDialog(this, "Depo başarıyla silindi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void toggleWarehouseStatus() {
        int selectedRow = warehouseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen durumunu değiştirmek istediğiniz depoyu seçin!",
                "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String warehouseId = (String) warehouseTableModel.getValueAt(selectedRow, 0);
        Warehouse warehouse = warehouseDAO.getWarehouseById(warehouseId);
        if (warehouse == null) {
            JOptionPane.showMessageDialog(this, "Depo bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentStatus = warehouse.isActive() ? "Aktif" : "Pasif";
        String newStatus = warehouse.isActive() ? "Pasif" : "Aktif";

        int result = JOptionPane.showConfirmDialog(this,
            "Depo durumunu '" + currentStatus + "' den '" + newStatus + "' e değiştirmek istiyor musunuz?",
            "Onay", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            warehouse.setActive(!warehouse.isActive());
            warehouseDAO.updateWarehouse(warehouse);
            loadWarehouses();
            JOptionPane.showMessageDialog(this,
                "Depo durumu başarıyla '" + newStatus + "' olarak değiştirildi!",
                "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Yardımcı metodlar
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Boş telefon kabul edilebilir
        }

        // Türkiye telefon formatı: 05XX XXX XX XX veya 05XXXXXXXXX veya +905XXXXXXXXX
        // Sadece rakamları al
        String cleaned = phone.replaceAll("[^0-9]", "");

        // 10 haneli cep telefonu (05XX) veya 11 haneli (905XX) veya 13 haneli (+905XX)
        if (cleaned.startsWith("90")) {
            cleaned = cleaned.substring(2); // 90'ı kaldır
        }

        // 05 ile başlamalı ve 11 hane olmalı
        return cleaned.matches("^05[0-9]{9}$");
    }

    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "";
        }

        // Sadece rakamları al
        String cleaned = phone.replaceAll("[^0-9]", "");

        if (cleaned.startsWith("90")) {
            cleaned = cleaned.substring(2);
        }

        // 05XX XXX XX XX formatında döndür
        if (cleaned.length() == 11) {
            return cleaned.substring(0, 4) + " " +
                   cleaned.substring(4, 7) + " " +
                   cleaned.substring(7, 9) + " " +
                   cleaned.substring(9, 11);
        }

        return phone; // Format uygun değilse olduğu gibi döndür
    }

    private JPanel createWarehousePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createStyledButton("Yeni Depo Ekle", new Color(46, 204, 113));
        addButton.addActionListener(e -> addWarehouse());
        buttonPanel.add(addButton);

        JButton editButton = createStyledButton("Düzenle", new Color(52, 152, 219));
        editButton.addActionListener(e -> editWarehouse());
        buttonPanel.add(editButton);

        JButton deleteButton = createStyledButton("Sil", new Color(231, 76, 60));
        deleteButton.addActionListener(e -> deleteWarehouse());
        buttonPanel.add(deleteButton);

        JButton toggleStatusButton = createStyledButton("Aktif/Pasif", new Color(243, 156, 18));
        toggleStatusButton.addActionListener(e -> toggleWarehouseStatus());
        buttonPanel.add(toggleStatusButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Depo Adı", "Depo Sorumlusu", "İl", "İlçe", "Mahalle", "Telefon 1", "Durum"};
        warehouseTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        warehouseTable = new JTable(warehouseTableModel);
        styleTable(warehouseTable);

        warehouseTable.getColumnModel().getColumn(0).setMinWidth(0);
        warehouseTable.getColumnModel().getColumn(0).setMaxWidth(0);
        warehouseTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(warehouseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(52, 152, 219, 50));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(236, 240, 241));
        header.setForeground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer(centerRenderer);
        }
    }

    private void loadData() {
        loadBranches();
        loadWarehouses();
    }

    private void loadBranches() {
        branchTableModel.setRowCount(0);
        List<Branch> branches = branchDAO.getAllBranches();
        for (Branch branch : branches) {
            branchTableModel.addRow(new Object[]{
                branch.getId(),
                branch.getName(),
                branch.getManagerName() != null ? branch.getManagerName() : "Atanmamış",
                branch.getProvince() != null ? branch.getProvince() : "-",
                branch.getDistrict() != null ? branch.getDistrict() : "-",
                branch.getNeighborhood() != null ? branch.getNeighborhood() : "-",
                branch.getPhone1() != null ? branch.getPhone1() : "-",
                branch.isActive() ? "Aktif" : "Pasif"
            });
        }
    }

    private void loadWarehouses() {
        warehouseTableModel.setRowCount(0);
        List<Warehouse> warehouses = warehouseDAO.getAllWarehouses();
        for (Warehouse warehouse : warehouses) {
            warehouseTableModel.addRow(new Object[]{
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getResponsibleName() != null ? warehouse.getResponsibleName() : "Atanmamış",
                warehouse.getProvince() != null ? warehouse.getProvince() : "-",
                warehouse.getDistrict() != null ? warehouse.getDistrict() : "-",
                warehouse.getNeighborhood() != null ? warehouse.getNeighborhood() : "-",
                warehouse.getPhone1() != null ? warehouse.getPhone1() : "-",
                warehouse.isActive() ? "Aktif" : "Pasif"
            });
        }
    }
}

