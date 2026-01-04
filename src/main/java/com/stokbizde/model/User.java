package com.stokbizde.model;

import java.time.LocalDateTime;

public class User {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private Role role;
    private String branchId; // Şube ile ilişki
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public enum Role {
        ADMIN("Yönetici"),
        BRANCH_MANAGER("Şube Müdürü"),
        WAREHOUSE_MANAGER("Depo Sorumlusu"),
        EMPLOYEE("Çalışan");

        private final String displayName;

        Role(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public User() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public User(String id, String username, String password, String fullName, Role role) {
        this();
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // Yetki kontrolü metodları
    public boolean hasRole(Role... roles) {
        for (Role r : roles) {
            if (this.role == r) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isBranchManager() {
        return role == Role.BRANCH_MANAGER;
    }

    public boolean isWarehouseManager() {
        return role == Role.WAREHOUSE_MANAGER;
    }

    public boolean canManageBranches() {
        return role == Role.ADMIN;
    }

    public boolean canManageUsers() {
        return role == Role.ADMIN;
    }

    public boolean canManageProducts() {
        return role == Role.ADMIN || role == Role.WAREHOUSE_MANAGER;
    }

    public boolean canViewReports() {
        return role == Role.ADMIN || role == Role.BRANCH_MANAGER;
    }

    public boolean canMakeTransfers() {
        return role != Role.EMPLOYEE; // Employee hariç herkes transfer yapabilir
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
}
