package com.stokbizde.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.stokbizde.model.User;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Updates.set;

public class UserDAO {
    private MongoCollection<User> collection;

    public UserDAO() {
        MongoDatabase database = DatabaseUtil.getDatabase();
        this.collection = database.getCollection("users", User.class);
    }

    public void addUser(User user) {
        if (user.getId() == null) {
            user.setId(new ObjectId().toString());
        }
        // Şifreyi hashle
        user.setPassword(hashPassword(user.getPassword()));
        collection.insertOne(user);
    }

    public User getUserByUsername(String username) {
        return collection.find(eq("username", username)).first();
    }

    public User authenticate(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && user.isActive()) {
            String hashedPassword = hashPassword(password);
            if (user.getPassword().equals(hashedPassword)) {
                updateLastLogin(user.getId());
                user.setLastLogin(LocalDateTime.now());
                return user;
            }
        }
        return null;
    }

    public void updateLastLogin(String userId) {
        collection.updateOne(
            eq("_id", userId),
            set("lastLogin", LocalDateTime.now())
        );
    }

    public void updateUser(User user) {
        Document updateDoc = new Document()
            .append("fullName", user.getFullName())
            .append("email", user.getEmail())
            .append("role", user.getRole().name())
            .append("branchId", user.getBranchId())
            .append("active", user.isActive());

        collection.updateOne(
            eq("_id", user.getId()),
            new Document("$set", updateDoc)
        );
    }

    public void changePassword(String userId, String newPassword) {
        collection.updateOne(
            eq("_id", userId),
            set("password", hashPassword(newPassword))
        );
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        collection.find().into(users);
        return users;
    }

    public List<User> getUsersByRole(User.Role role) {
        List<User> users = new ArrayList<>();
        collection.find(eq("role", role.name())).into(users);
        return users;
    }

    public List<User> getUsersByBranch(String branchId) {
        List<User> users = new ArrayList<>();
        collection.find(eq("branchId", branchId)).into(users);
        return users;
    }

    public void deleteUser(String userId) {
        // Soft delete - kullanıcıyı pasif yap
        collection.updateOne(
            eq("_id", userId),
            set("active", false)
        );
    }

    // Şifre hashleme (SHA-256)
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Şifre hashleme hatası", e);
        }
    }

    // İlk admin kullanıcısını oluştur
    public void createDefaultAdmin() {
        User existingAdmin = collection.find(eq("username", "admin")).first();
        if (existingAdmin == null) {
            User admin = new User();
            admin.setId(new ObjectId().toString());
            admin.setUsername("admin");
            admin.setPassword("admin123"); // İlk şifre
            admin.setFullName("Sistem Yöneticisi");
            admin.setEmail("admin@stokbizde.com");
            admin.setRole(User.Role.ADMIN);
            addUser(admin);
        }
    }
}
