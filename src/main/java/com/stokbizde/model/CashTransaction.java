package com.stokbizde.model;

import java.time.LocalDateTime;

public class CashTransaction {
    private String id;
    private String cashRegisterId;      // Kasa ID
    private String cashRegisterName;    // Kasa adı
    private TransactionType type;       // İşlem tipi (Tahsilat/Ödeme)
    private double amount;              // Tutar
    private String currency;            // Para birimi
    private String description;         // Açıklama
    private String referenceNumber;     // Referans numarası (fiş no vb.)
    private String customerId;          // Müşteri/Tedarikçi ID
    private String customerName;        // Müşteri/Tedarikçi adı
    private LocalDateTime transactionDate; // İşlem tarihi
    private String userId;              // İşlemi yapan kullanıcı
    private String userName;            // Kullanıcı adı
    private String branchId;            // Şube ID
    private String branchName;          // Şube adı
    private double balanceAfter;        // İşlem sonrası bakiye

    // İşlem Tipleri
    public enum TransactionType {
        TAHSILAT("Tahsilat", 1),         // Giriş
        ODEME("Ödeme", -1),              // Çıkış
        DEVIR("Devir", 1),               // Devir
        DUZELTME("Düzeltme", 0);         // Düzeltme

        private final String displayName;
        private final int multiplier;

        TransactionType(String displayName, int multiplier) {
            this.displayName = displayName;
            this.multiplier = multiplier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMultiplier() {
            return multiplier;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    // Constructors
    public CashTransaction() {
        this.transactionDate = LocalDateTime.now();
        this.currency = "TRY";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCashRegisterId() {
        return cashRegisterId;
    }

    public void setCashRegisterId(String cashRegisterId) {
        this.cashRegisterId = cashRegisterId;
    }

    public String getCashRegisterName() {
        return cashRegisterName;
    }

    public void setCashRegisterName(String cashRegisterName) {
        this.cashRegisterName = cashRegisterName;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
}

