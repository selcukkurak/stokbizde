package com.stokbizde.ui;

import com.stokbizde.dao.BranchDAO;
import com.stokbizde.dao.UserDAO;
import com.stokbizde.model.Branch;
import com.stokbizde.model.User;
import com.stokbizde.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private UserDAO userDAO;
    private final BranchDAO branchDAO;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton changePasswordButton;

    public UserManagementPanel() {
        this.userDAO = new UserDAO();
        this.branchDAO = new BranchDAO();

        // Yetki kontrolü
        if (!SessionManager.getInstance().canManageUsers()) {
            setLayout(new BorderLayout());
            JLabel accessDenied = new JLabel("Bu sayfaya erişim yetkiniz yok!", SwingConstants.CENTER);
            accessDenied.setFont(new Font("Segoe UI", Font.BOLD, 18));
            accessDenied.setForeground(Color.RED);
            add(accessDenied, BorderLayout.CENTER);
            return;
        }

        initComponents();
        loadUsers();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // Üst panel - Başlık ve butonlar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Kullanıcı Yönetimi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        addButton = createButton("Yeni Kullanıcı", new Color(41, 98, 255));
        addButton.addActionListener(e -> showAddUserDialog());

        editButton = createButton("Düzenle", new Color(76, 175, 80));
        editButton.addActionListener(e -> showEditUserDialog());
        editButton.setEnabled(false);

        changePasswordButton = createButton("Şifre Değiştir", new Color(255, 152, 0));
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        changePasswordButton.setEnabled(false);

        deleteButton = createButton("Pasif Yap", new Color(244, 67, 54));
        deleteButton.addActionListener(e -> deactivateUser());
        deleteButton.setEnabled(false);

        refreshButton = createButton("Yenile", new Color(96, 125, 139));
        refreshButton.addActionListener(e -> loadUsers());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Tablo
        String[] columns = {"ID", "Kullanıcı Adı", "Ad Soyad", "E-posta", "Rol", "Şube", "Durum", "Son Giriş"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.setRowHeight(30);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(new Color(245, 245, 245));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = userTable.getSelectedRow() != -1;
            editButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
            changePasswordButton.setEnabled(selected);
        });

        // ID sütununu gizle
        userTable.getColumnModel().getColumn(0).setMinWidth(0);
        userTable.getColumnModel().getColumn(0).setMaxWidth(0);
        userTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (User user : users) {
            String branchName = "-";
            if (user.getBranchId() != null) {
                Branch branch = branchDAO.getBranchById(user.getBranchId());
                if (branch != null) {
                    branchName = branch.getName();
                }
            }

            String lastLogin = user.getLastLogin() != null
                ? user.getLastLogin().format(formatter)
                : "Hiç giriş yapmadı";

            tableModel.addRow(new Object[]{
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail() != null ? user.getEmail() : "-",
                user.getRole().getDisplayName(),
                branchName,
                user.isActive() ? "Aktif" : "Pasif",
                lastLogin
            });
        }
    }

    private void showAddUserDialog() {
        UserDialog dialog = new UserDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadUsers();
        }
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) return;

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        User user = userDAO.getAllUsers().stream()
            .filter(u -> u.getId().equals(userId))
            .findFirst()
            .orElse(null);

        if (user != null) {
            UserDialog dialog = new UserDialog((Frame) SwingUtilities.getWindowAncestor(this), user);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadUsers();
            }
        }
    }

    private void showChangePasswordDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) return;

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Kullanıcı:"));
        panel.add(new JLabel(username));
        panel.add(new JLabel("Yeni Şifre:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Şifre Tekrar:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Şifre Değiştir",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Şifre boş olamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Şifreler eşleşmiyor!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            userDAO.changePassword(userId, newPassword);
            JOptionPane.showMessageDialog(this, "Şifre başarıyla değiştirildi!", "Başarılı",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deactivateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) return;

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            username + " kullanıcısını pasif yapmak istediğinize emin misiniz?",
            "Onay", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            userDAO.deleteUser(userId);
            JOptionPane.showMessageDialog(this, "Kullanıcı pasif yapıldı!", "Başarılı",
                JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
        }
    }

    // İç sınıf - Kullanıcı ekleme/düzenleme dialogu
    private class UserDialog extends JDialog {
        private JTextField usernameField;
        private JTextField fullNameField;
        private JTextField emailField;
        private JPasswordField passwordField;
        private JComboBox<User.Role> roleCombo;
        private JComboBox<String> branchCombo;
        private JCheckBox activeCheckBox;
        private User user;
        private boolean saved = false;

        public UserDialog(Frame parent, User user) {
            super(parent, user == null ? "Yeni Kullanıcı" : "Kullanıcı Düzenle", true);
            this.user = user;
            initDialog();
            setLocationRelativeTo(parent);
        }

        private void initDialog() {
            setLayout(new BorderLayout(10, 10));
            setSize(450, 500);

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            int row = 0;

            // Kullanıcı adı
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Kullanıcı Adı:"), gbc);
            gbc.gridx = 1;
            usernameField = new JTextField(20);
            usernameField.setEnabled(user == null); // Sadece yeni kullanıcıda değiştirilebilir
            formPanel.add(usernameField, gbc);
            row++;

            // Ad Soyad
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Ad Soyad:"), gbc);
            gbc.gridx = 1;
            fullNameField = new JTextField(20);
            formPanel.add(fullNameField, gbc);
            row++;

            // E-posta
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("E-posta:"), gbc);
            gbc.gridx = 1;
            emailField = new JTextField(20);
            formPanel.add(emailField, gbc);
            row++;

            // Şifre (sadece yeni kullanıcı için)
            if (user == null) {
                gbc.gridx = 0; gbc.gridy = row;
                formPanel.add(new JLabel("Şifre:"), gbc);
                gbc.gridx = 1;
                passwordField = new JPasswordField(20);
                formPanel.add(passwordField, gbc);
                row++;
            }

            // Rol
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Rol:"), gbc);
            gbc.gridx = 1;
            roleCombo = new JComboBox<>(User.Role.values());
            roleCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof User.Role) {
                        setText(((User.Role) value).getDisplayName());
                    }
                    return this;
                }
            });
            formPanel.add(roleCombo, gbc);
            row++;

            // Şube
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Şube:"), gbc);
            gbc.gridx = 1;
            branchCombo = new JComboBox<>();
            branchCombo.addItem("Seçiniz");
            List<Branch> branches = branchDAO.getAllBranches();
            for (Branch branch : branches) {
                branchCombo.addItem(branch.getId() + "|" + branch.getName());
            }
            formPanel.add(branchCombo, gbc);
            row++;

            // Aktif
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Durum:"), gbc);
            gbc.gridx = 1;
            activeCheckBox = new JCheckBox("Aktif");
            activeCheckBox.setSelected(true);
            formPanel.add(activeCheckBox, gbc);

            // Mevcut kullanıcı verilerini doldur
            if (user != null) {
                usernameField.setText(user.getUsername());
                fullNameField.setText(user.getFullName());
                emailField.setText(user.getEmail());
                roleCombo.setSelectedItem(user.getRole());
                activeCheckBox.setSelected(user.isActive());

                if (user.getBranchId() != null) {
                    for (int i = 1; i < branchCombo.getItemCount(); i++) {
                        String item = branchCombo.getItemAt(i);
                        if (item.startsWith(user.getBranchId() + "|")) {
                            branchCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }

            // Butonlar
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Kaydet");
            saveButton.addActionListener(e -> save());
            JButton cancelButton = new JButton("İptal");
            cancelButton.addActionListener(e -> dispose());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void save() {
            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();

            if (username.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kullanıcı adı ve ad soyad zorunludur!",
                    "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (user == null) {
                // Yeni kullanıcı
                String password = new String(passwordField.getPassword());
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Şifre zorunludur!",
                        "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setFullName(fullName);
                user.setEmail(email.isEmpty() ? null : email);
                user.setRole((User.Role) roleCombo.getSelectedItem());
                user.setActive(activeCheckBox.isSelected());

                String selectedBranch = (String) branchCombo.getSelectedItem();
                if (selectedBranch != null && !selectedBranch.equals("Seçiniz")) {
                    user.setBranchId(selectedBranch.split("\\|")[0]);
                }

                userDAO.addUser(user);
            } else {
                // Kullanıcı güncelle
                user.setFullName(fullName);
                user.setEmail(email.isEmpty() ? null : email);
                user.setRole((User.Role) roleCombo.getSelectedItem());
                user.setActive(activeCheckBox.isSelected());

                String selectedBranch = (String) branchCombo.getSelectedItem();
                if (selectedBranch != null && !selectedBranch.equals("Seçiniz")) {
                    user.setBranchId(selectedBranch.split("\\|")[0]);
                } else {
                    user.setBranchId(null);
                }

                userDAO.updateUser(user);
            }

            saved = true;
            dispose();
        }

        public boolean isSaved() {
            return saved;
        }
    }
}

