package com.stokbizde.util;

import com.stokbizde.model.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasRole(User.Role... roles) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.hasRole(roles);
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public boolean canManageBranches() {
        return currentUser != null && currentUser.canManageBranches();
    }

    public boolean canManageUsers() {
        return currentUser != null && currentUser.canManageUsers();
    }

    public boolean canManageProducts() {
        return currentUser != null && currentUser.canManageProducts();
    }

    public boolean canViewReports() {
        return currentUser != null && currentUser.canViewReports();
    }

    public boolean canMakeTransfers() {
        return currentUser != null && currentUser.canMakeTransfers();
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "Misafir";
    }

    public String getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    public String getCurrentUserFullName() {
        return currentUser != null ? currentUser.getFullName() : "Misafir";
    }

    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole().getDisplayName() : "";
    }
}

