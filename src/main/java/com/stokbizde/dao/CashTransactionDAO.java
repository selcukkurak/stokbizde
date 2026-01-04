package com.stokbizde.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.stokbizde.model.CashTransaction;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CashTransactionDAO {
    private final MongoCollection<Document> collection;

    public CashTransactionDAO() {
        MongoDatabase database = DatabaseUtil.getDatabase();
        this.collection = database.getCollection("cash_transactions");
    }

    // İşlem kaydet
    public String save(CashTransaction transaction) {
        Document doc = new Document()
                .append("cashRegisterId", transaction.getCashRegisterId())
                .append("cashRegisterName", transaction.getCashRegisterName())
                .append("type", transaction.getType() != null ? transaction.getType().name() : null)
                .append("amount", transaction.getAmount())
                .append("currency", transaction.getCurrency())
                .append("description", transaction.getDescription())
                .append("referenceNumber", transaction.getReferenceNumber())
                .append("customerId", transaction.getCustomerId())
                .append("customerName", transaction.getCustomerName())
                .append("transactionDate", transaction.getTransactionDate())
                .append("userId", transaction.getUserId())
                .append("userName", transaction.getUserName())
                .append("branchId", transaction.getBranchId())
                .append("branchName", transaction.getBranchName())
                .append("balanceAfter", transaction.getBalanceAfter());

        if (transaction.getId() != null && !transaction.getId().isEmpty()) {
            collection.replaceOne(
                    Filters.eq("_id", new ObjectId(transaction.getId())),
                    doc
            );
            return transaction.getId();
        } else {
            collection.insertOne(doc);
            return doc.getObjectId("_id").toString();
        }
    }

    // ID'ye göre işlem bul
    public CashTransaction findById(String id) {
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return doc != null ? documentToTransaction(doc) : null;
    }

    // Kasaya göre işlemleri getir
    public List<CashTransaction> findByCashRegister(String cashRegisterId) {
        List<CashTransaction> transactions = new ArrayList<>();
        collection.find(Filters.eq("cashRegisterId", cashRegisterId))
                .sort(Sorts.descending("transactionDate"))
                .forEach(doc -> transactions.add(documentToTransaction(doc)));
        return transactions;
    }

    // Şubeye göre işlemleri getir
    public List<CashTransaction> findByBranch(String branchId) {
        List<CashTransaction> transactions = new ArrayList<>();
        collection.find(Filters.eq("branchId", branchId))
                .sort(Sorts.descending("transactionDate"))
                .forEach(doc -> transactions.add(documentToTransaction(doc)));
        return transactions;
    }

    // Tarih aralığına göre işlemleri getir
    public List<CashTransaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<CashTransaction> transactions = new ArrayList<>();
        collection.find(Filters.and(
                        Filters.gte("transactionDate", startDate),
                        Filters.lte("transactionDate", endDate)
                ))
                .sort(Sorts.descending("transactionDate"))
                .forEach(doc -> transactions.add(documentToTransaction(doc)));
        return transactions;
    }

    // Kasa ve tarih aralığına göre işlemleri getir
    public List<CashTransaction> findByCashRegisterAndDateRange(
            String cashRegisterId, LocalDateTime startDate, LocalDateTime endDate) {
        List<CashTransaction> transactions = new ArrayList<>();
        Bson filter = Filters.and(
                Filters.eq("cashRegisterId", cashRegisterId),
                Filters.gte("transactionDate", startDate),
                Filters.lte("transactionDate", endDate)
        );
        collection.find(filter)
                .sort(Sorts.descending("transactionDate"))
                .forEach(doc -> transactions.add(documentToTransaction(doc)));
        return transactions;
    }

    // Tüm işlemleri getir
    public List<CashTransaction> findAll() {
        List<CashTransaction> transactions = new ArrayList<>();
        collection.find()
                .sort(Sorts.descending("transactionDate"))
                .limit(1000) // Performans için limit
                .forEach(doc -> transactions.add(documentToTransaction(doc)));
        return transactions;
    }

    // İşlem sil
    public boolean delete(String id) {
        return collection.deleteOne(Filters.eq("_id", new ObjectId(id))).getDeletedCount() > 0;
    }

    // Document'i CashTransaction'a çevir
    private CashTransaction documentToTransaction(Document doc) {
        CashTransaction transaction = new CashTransaction();
        transaction.setId(doc.getObjectId("_id").toString());
        transaction.setCashRegisterId(doc.getString("cashRegisterId"));
        transaction.setCashRegisterName(doc.getString("cashRegisterName"));

        String typeStr = doc.getString("type");
        if (typeStr != null) {
            transaction.setType(CashTransaction.TransactionType.valueOf(typeStr));
        }

        transaction.setAmount(doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0);
        transaction.setCurrency(doc.getString("currency"));
        transaction.setDescription(doc.getString("description"));
        transaction.setReferenceNumber(doc.getString("referenceNumber"));
        transaction.setCustomerId(doc.getString("customerId"));
        transaction.setCustomerName(doc.getString("customerName"));

        // Date'i LocalDateTime'a çevir
        Date transactionDate = doc.getDate("transactionDate");
        if (transactionDate != null) {
            transaction.setTransactionDate(transactionDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }

        transaction.setUserId(doc.getString("userId"));
        transaction.setUserName(doc.getString("userName"));
        transaction.setBranchId(doc.getString("branchId"));
        transaction.setBranchName(doc.getString("branchName"));
        transaction.setBalanceAfter(doc.getDouble("balanceAfter") != null ? doc.getDouble("balanceAfter") : 0.0);

        return transaction;
    }

    // Kasa bazında toplam hesapla
    public double calculateBalance(String cashRegisterId) {
        List<CashTransaction> transactions = findByCashRegister(cashRegisterId);
        double balance = 0.0;
        for (CashTransaction transaction : transactions) {
            if (transaction.getType() != null) {
                balance += transaction.getAmount() * transaction.getType().getMultiplier();
            }
        }
        return balance;
    }
}

