package com.stokbizde.ui;

import com.stokbizde.dao.BranchDAO;
import com.stokbizde.dao.ProductDAO;
import com.stokbizde.dao.TransferDAO;
import com.stokbizde.model.Branch;
import com.stokbizde.model.Product;
import com.stokbizde.model.Transfer;
import com.stokbizde.util.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class TransferManagementPanel extends JPanel {
    private TransferDAO transferDAO = new TransferDAO();
    private BranchDAO branchDAO = new BranchDAO();
    private ProductDAO productDAO = new ProductDAO();
    private JTable transferTable;
    private JComboBox<Branch> fromBranchCombo, toBranchCombo;
    private JComboBox<Product> productCombo;
    private JTextField quantityField;
    private JComboBox<Transfer.TransferType> typeCombo;
    private DefaultTableModel tableModel;

    public TransferManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Yetki kontrolü
        if (!SessionManager.getInstance().canMakeTransfers()) {
            setLayout(new BorderLayout());
            JLabel accessDenied = new JLabel("Bu sayfaya erişim yetkiniz yok!", SwingConstants.CENTER);
            accessDenied.setFont(new Font("Segoe UI", Font.BOLD, 18));
            accessDenied.setForeground(Color.RED);
            add(accessDenied, BorderLayout.CENTER);
            return;
        }

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Yeni Transfer Ekle"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Gönderen Şube:"), gbc);
        gbc.gridx = 1;
        fromBranchCombo = new JComboBox<>();
        formPanel.add(fromBranchCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Alan Şube:"), gbc);
        gbc.gridx = 1;
        toBranchCombo = new JComboBox<>();
        formPanel.add(toBranchCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Ürün:"), gbc);
        gbc.gridx = 1;
        productCombo = new JComboBox<>();
        formPanel.add(productCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Miktar:"), gbc);
        gbc.gridx = 1;
        quantityField = new JTextField(10);
        formPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Tip:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(Transfer.TransferType.values());
        formPanel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton addButton = new JButton("Transfer Ekle");
        addButton.addActionListener(e -> addTransfer());
        formPanel.add(addButton, gbc);

        add(formPanel, BorderLayout.WEST);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Gönderen", "Alan", "Ürün", "Miktar", "Tip", "Durum", "Tarih"}, 0);
        transferTable = new JTable(tableModel);
        transferTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transferTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(transferTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transferler"));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton completeButton = new JButton("Tamamla");
        completeButton.addActionListener(e -> completeTransfer());
        buttonPanel.add(completeButton);
        JButton refreshButton = new JButton("Yenile");
        refreshButton.addActionListener(e -> loadTransfers());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadCombos();
        loadTransfers();
    }

    private void loadCombos() {
        try {
            List<Branch> branches = branchDAO.getAllBranches();
            fromBranchCombo.removeAllItems();
            toBranchCombo.removeAllItems();
            for (Branch b : branches) {
                fromBranchCombo.addItem(b);
                toBranchCombo.addItem(b);
            }
            List<Product> products = productDAO.getAllProducts();
            productCombo.removeAllItems();
            for (Product p : products) {
                productCombo.addItem(p);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }

    private void addTransfer() {
        try {
            Branch from = (Branch) fromBranchCombo.getSelectedItem();
            Branch to = (Branch) toBranchCombo.getSelectedItem();
            Product product = (Product) productCombo.getSelectedItem();
            if (from == null || to == null || product == null) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları seçin.");
                return;
            }
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Miktar pozitif bir sayı olmalı.");
                return;
            }
            Transfer.TransferType type = (Transfer.TransferType) typeCombo.getSelectedItem();
            Transfer transfer = new Transfer(null, from.getId(), to.getId(), product.getId(), quantity, type, Transfer.TransferStatus.PENDING, new Date());
            transferDAO.addTransfer(transfer);
            loadTransfers();
            quantityField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Miktar geçerli bir sayı olmalı.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }

    private void completeTransfer() {
        int selectedRow = transferTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                String id = (String) tableModel.getValueAt(selectedRow, 0);
                transferDAO.updateTransferStatus(id, Transfer.TransferStatus.COMPLETED);
                loadTransfers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir transfer seçin.");
        }
    }

    private void loadTransfers() {
        try {
            tableModel.setRowCount(0);
            List<Transfer> transfers = transferDAO.getAllTransfers();
            for (Transfer t : transfers) {
                tableModel.addRow(new Object[]{t.getId(), t.getFromBranchId(), t.getToBranchId(), t.getProductId(), t.getQuantity(), t.getType().toString(), t.getStatus().toString(), t.getDate().toString()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }
}
