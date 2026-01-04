package com.stokbizde.model;

import java.time.LocalDateTime;

public class CashRegister {
    private String id;
    private String name;              // Kasa Adı
    private String code;              // Kasa Kodu (F2, F4, vb.)
    private CashRegisterType type;    // Kasa Tipi
    private String branchId;          // Bağlı olduğu şube ID
    private String branchName;        // Şube adı (görüntüleme için)
    private String warehouseId;       // Bağlı olduğu depo ID (opsiyonel)
    private String warehouseName;     // Depo adı (görüntüleme için)
    private double balance;           // Güncel bakiye
    private String currency;          // Para birimi (TRY, USD, EUR)
    private boolean active;           // Aktif/Pasif
    private String description;       // Açıklama
    private LocalDateTime createdAt;  // Oluşturma tarihi
    private String createdBy;         // Oluşturan kullanıcı

    // Kasa Tipleri
    public enum CashRegisterType {
        NAKIT_ODEME("Nakit Ödeme"),
        KREDI_KARTI("Kredi Kartı"),
        CEK("Çek"),
        PARA_PUAN("Para Puan"),
        BANKA("Banka");

        private final String displayName;

        CashRegisterType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    // Constructors
    public CashRegister() {
        this.active = true;
        this.balance = 0.0;
        this.currency = "TRY";
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CashRegisterType getType() {
        return type;
    }

    public void setType(CashRegisterType type) {
        this.type = type;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}

