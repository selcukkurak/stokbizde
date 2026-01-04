package com.stokbizde.ui;

import com.stokbizde.dao.BranchDAO;
import com.stokbizde.dao.CashRegisterDAO;
import com.stokbizde.dao.CashTransactionDAO;
import com.stokbizde.dao.WarehouseDAO;
import com.stokbizde.model.Branch;
import com.stokbizde.model.CashRegister;
import com.stokbizde.model.CashTransaction;
import com.stokbizde.model.Warehouse;
import com.stokbizde.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CashRegisterManagementPanel extends JPanel {
    private final CashRegisterDAO cashRegisterDAO;
    private final CashTransactionDAO transactionDAO;
    private final BranchDAO branchDAO;
    private final WarehouseDAO warehouseDAO;

    private JTabbedPane tabbedPane;
    private JTable cashRegisterTable;
    private JTable transactionTable;
    private DefaultTableModel cashRegisterTableModel;
    private DefaultTableModel transactionTableModel;

    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public CashRegisterManagementPanel() {
        this.cashRegisterDAO = new CashRegisterDAO();
        this.transactionDAO = new CashTransactionDAO();
        this.branchDAO = new BranchDAO();
        this.warehouseDAO = new WarehouseDAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initComponents();
        loadCashRegisters();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Kasalar Sekmesi
        JPanel cashRegisterPanel = createCashRegisterPanel();
        tabbedPane.addTab("Kasalar", cashRegisterPanel);

        // Kasa Hareketleri Sekmesi
        JPanel transactionPanel = createTransactionPanel();
        tabbedPane.addTab("Kasa Hareketleri", transactionPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCashRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Başlık
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Kasa Yönetimi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Yeni Kasa");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.addActionListener(e -> showCashRegisterDialog(null));

        JButton editButton = new JButton("Düzenle");
        editButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editButton.addActionListener(e -> editSelectedCashRegister());

        JButton deleteButton = new JButton("Sil");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.addActionListener(e -> deleteSelectedCashRegister());

        JButton refreshButton = new JButton("Yenile");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> loadCashRegisters());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Tablo
        String[] columns = {"Kasa Adı", "Kod", "Tip", "Şube", "Depo", "Bakiye", "Para Birimi", "Durum"};
        cashRegisterTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cashRegisterTable = new JTable(cashRegisterTableModel);
        cashRegisterTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cashRegisterTable.setRowHeight(30);
        cashRegisterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cashRegisterTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        cashRegisterTable.setGridColor(new Color(230, 230, 230));

        // Bakiye kolonunu sağa hizala
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        cashRegisterTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        // Çift tıklama ile düzenleme
        cashRegisterTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editSelectedCashRegister();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(cashRegisterTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Başlık ve Filtre
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Kasa Hareketleri");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Filtre paneli
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(Color.WHITE);

        JComboBox<String> cashRegisterCombo = new JComboBox<>();
        cashRegisterCombo.addItem("Tüm Kasalar");
        cashRegisterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cashRegisterCombo.setPreferredSize(new Dimension(200, 30));

        JButton newTransactionButton = new JButton("Yeni Hareket");
        newTransactionButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newTransactionButton.addActionListener(e -> showTransactionDialog(null));

        JButton refreshButton = new JButton("Yenile");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> loadTransactions(null));

        filterPanel.add(new JLabel("Kasa: "));
        filterPanel.add(cashRegisterCombo);
        filterPanel.add(newTransactionButton);
        filterPanel.add(refreshButton);

        headerPanel.add(filterPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Tablo
        String[] columns = {"Tarih", "Kasa", "İşlem Tipi", "Tutar", "Para Birimi", "Açıklama", "Kullanıcı", "Bakiye"};
        transactionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(transactionTableModel);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        transactionTable.setRowHeight(30);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        transactionTable.setGridColor(new Color(230, 230, 230));

        // Tutar ve bakiye kolonlarını sağa hizala
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        transactionTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        transactionTable.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Kasa seçildiğinde hareketleri filtrele
        cashRegisterCombo.addActionListener(e -> {
            String selected = (String) cashRegisterCombo.getSelectedItem();
            if ("Tüm Kasalar".equals(selected)) {
                loadTransactions(null);
            } else {
                // Seçilen kasanın ID'sini bul ve filtrele
                loadTransactions(null); // TODO: Implement filtering
            }
        });

        return panel;
    }

    private void loadCashRegisters() {
        cashRegisterTableModel.setRowCount(0);
        List<CashRegister> cashRegisters = cashRegisterDAO.findAll();

        for (CashRegister cr : cashRegisters) {
            cashRegisterTableModel.addRow(new Object[]{
                    cr.getName(),
                    cr.getCode(),
                    cr.getType() != null ? cr.getType().getDisplayName() : "",
                    cr.getBranchName() != null ? cr.getBranchName() : "",
                    cr.getWarehouseName() != null ? cr.getWarehouseName() : "",
                    decimalFormat.format(cr.getBalance()),
                    cr.getCurrency(),
                    cr.isActive() ? "Aktif" : "Pasif"
            });
        }
    }

    private void loadTransactions(String cashRegisterId) {
        transactionTableModel.setRowCount(0);
        List<CashTransaction> transactions;

        if (cashRegisterId != null && !cashRegisterId.isEmpty()) {
            transactions = transactionDAO.findByCashRegister(cashRegisterId);
        } else {
            transactions = transactionDAO.findAll();
        }

        for (CashTransaction tr : transactions) {
            transactionTableModel.addRow(new Object[]{
                    tr.getTransactionDate() != null ? tr.getTransactionDate().format(dateFormatter) : "",
                    tr.getCashRegisterName(),
                    tr.getType() != null ? tr.getType().getDisplayName() : "",
                    decimalFormat.format(tr.getAmount()),
                    tr.getCurrency(),
                    tr.getDescription(),
                    tr.getUserName(),
                    decimalFormat.format(tr.getBalanceAfter())
            });
        }
    }

    private void showCashRegisterDialog(CashRegister cashRegister) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                cashRegister == null ? "Yeni Kasa" : "Kasa Düzenle", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form alanları
        JTextField nameField = new JTextField(20);
        JTextField codeField = new JTextField(10);
        JComboBox<CashRegister.CashRegisterType> typeCombo = new JComboBox<>(CashRegister.CashRegisterType.values());
        JComboBox<Branch> branchCombo = new JComboBox<>();
        JComboBox<Warehouse> warehouseCombo = new JComboBox<>();
        JTextField balanceField = new JTextField(10);
        JComboBox<String> currencyCombo = new JComboBox<>(new String[]{"TRY", "USD", "EUR"});
        JCheckBox activeCheck = new JCheckBox("Aktif");
        JTextArea descriptionArea = new JTextArea(3, 20);

        // Şubeleri yükle
        branchCombo.addItem(null);
        List<Branch> branches = branchDAO.findAll();
        for (Branch b : branches) {
            branchCombo.addItem(b);
        }

        // Depoları yükle
        warehouseCombo.addItem(null);
        branchCombo.addActionListener(e -> {
            Branch selectedBranch = (Branch) branchCombo.getSelectedItem();
            warehouseCombo.removeAllItems();
            warehouseCombo.addItem(null);
            if (selectedBranch != null) {
                List<Warehouse> warehouses = warehouseDAO.findByBranch(selectedBranch.getId());
                for (Warehouse w : warehouses) {
                    warehouseCombo.addItem(w);
                }
            }
        });

        // Mevcut kasa varsa alanları doldur
        if (cashRegister != null) {
            nameField.setText(cashRegister.getName());
            codeField.setText(cashRegister.getCode());
            if (cashRegister.getType() != null) {
                typeCombo.setSelectedItem(cashRegister.getType());
            }
            balanceField.setText(String.valueOf(cashRegister.getBalance()));
            currencyCombo.setSelectedItem(cashRegister.getCurrency());
            activeCheck.setSelected(cashRegister.isActive());
            descriptionArea.setText(cashRegister.getDescription());

            // Şube ve depo seçimini ayarla
            for (int i = 0; i < branchCombo.getItemCount(); i++) {
                Branch b = branchCombo.getItemAt(i);
                if (b != null && b.getId().equals(cashRegister.getBranchId())) {
                    branchCombo.setSelectedItem(b);
                    break;
                }
            }
        } else {
            activeCheck.setSelected(true);
            balanceField.setText("0.00");
            currencyCombo.setSelectedItem("TRY");
        }

        // Form elemanlarını ekle
        int row = 0;
        addFormField(formPanel, gbc, row++, "Kasa Adı:", nameField);
        addFormField(formPanel, gbc, row++, "Kasa Kodu:", codeField);
        addFormField(formPanel, gbc, row++, "Kasa Tipi:", typeCombo);
        addFormField(formPanel, gbc, row++, "Şube:", branchCombo);
        addFormField(formPanel, gbc, row++, "Depo:", warehouseCombo);
        addFormField(formPanel, gbc, row++, "Başlangıç Bakiyesi:", balanceField);
        addFormField(formPanel, gbc, row++, "Para Birimi:", currencyCombo);
        addFormField(formPanel, gbc, row++, "", activeCheck);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Açıklama:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Kaydet");
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Kasa adı boş olamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            CashRegister cr = cashRegister != null ? cashRegister : new CashRegister();
            cr.setName(nameField.getText().trim());
            cr.setCode(codeField.getText().trim());
            cr.setType((CashRegister.CashRegisterType) typeCombo.getSelectedItem());

            Branch selectedBranch = (Branch) branchCombo.getSelectedItem();
            if (selectedBranch != null) {
                cr.setBranchId(selectedBranch.getId());
                cr.setBranchName(selectedBranch.getName());
            }

            Warehouse selectedWarehouse = (Warehouse) warehouseCombo.getSelectedItem();
            if (selectedWarehouse != null) {
                cr.setWarehouseId(selectedWarehouse.getId());
                cr.setWarehouseName(selectedWarehouse.getName());
            }

            try {
                cr.setBalance(Double.parseDouble(balanceField.getText().replace(",", ".")));
            } catch (NumberFormatException ex) {
                cr.setBalance(0.0);
            }

            cr.setCurrency((String) currencyCombo.getSelectedItem());
            cr.setActive(activeCheck.isSelected());
            cr.setDescription(descriptionArea.getText().trim());

            if (cashRegister == null) {
                cr.setCreatedBy(SessionManager.getInstance().getCurrentUserId());
            }

            cashRegisterDAO.save(cr);
            loadCashRegisters();
            dialog.dispose();
        });

        JButton cancelButton = new JButton("İptal");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showTransactionDialog(CashTransaction transaction) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Yeni Kasa Hareketi", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form alanları
        JComboBox<CashRegister> cashRegisterCombo = new JComboBox<>();
        List<CashRegister> cashRegisters = cashRegisterDAO.findActive();
        for (CashRegister cr : cashRegisters) {
            cashRegisterCombo.addItem(cr);
        }

        JComboBox<CashTransaction.TransactionType> typeCombo = new JComboBox<>(CashTransaction.TransactionType.values());
        JTextField amountField = new JTextField(10);
        JTextArea descriptionArea = new JTextArea(3, 20);

        int row = 0;
        addFormField(formPanel, gbc, row++, "Kasa:", cashRegisterCombo);
        addFormField(formPanel, gbc, row++, "İşlem Tipi:", typeCombo);
        addFormField(formPanel, gbc, row++, "Tutar:", amountField);

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Açıklama:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Kaydet");
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.addActionListener(e -> {
            CashRegister selectedCashRegister = (CashRegister) cashRegisterCombo.getSelectedItem();
            if (selectedCashRegister == null) {
                JOptionPane.showMessageDialog(dialog, "Kasa seçiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double amount = Double.parseDouble(amountField.getText().replace(",", "."));

                CashTransaction tr = new CashTransaction();
                tr.setCashRegisterId(selectedCashRegister.getId());
                tr.setCashRegisterName(selectedCashRegister.getName());
                tr.setType((CashTransaction.TransactionType) typeCombo.getSelectedItem());
                tr.setAmount(amount);
                tr.setCurrency(selectedCashRegister.getCurrency());
                tr.setDescription(descriptionArea.getText().trim());
                tr.setUserId(SessionManager.getInstance().getCurrentUserId());
                tr.setUserName(SessionManager.getInstance().getCurrentUserFullName());
                tr.setBranchId(selectedCashRegister.getBranchId());
                tr.setBranchName(selectedCashRegister.getBranchName());

                // Yeni bakiyeyi hesapla
                double newBalance = selectedCashRegister.getBalance() + (amount * tr.getType().getMultiplier());
                tr.setBalanceAfter(newBalance);

                // İşlemi kaydet
                transactionDAO.save(tr);

                // Kasa bakiyesini güncelle
                cashRegisterDAO.updateBalance(selectedCashRegister.getId(), newBalance);

                loadTransactions(null);
                loadCashRegisters();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Geçerli bir tutar giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("İptal");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editSelectedCashRegister() {
        int selectedRow = cashRegisterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen düzenlemek için bir kasa seçin!",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<CashRegister> cashRegisters = cashRegisterDAO.findAll();
        if (selectedRow < cashRegisters.size()) {
            showCashRegisterDialog(cashRegisters.get(selectedRow));
        }
    }

    private void deleteSelectedCashRegister() {
        int selectedRow = cashRegisterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silmek için bir kasa seçin!",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Seçili kasayı silmek istediğinize emin misiniz?",
                "Onay", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            List<CashRegister> cashRegisters = cashRegisterDAO.findAll();
            if (selectedRow < cashRegisters.size()) {
                cashRegisterDAO.delete(cashRegisters.get(selectedRow).getId());
                loadCashRegisters();
            }
        }
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }
}

