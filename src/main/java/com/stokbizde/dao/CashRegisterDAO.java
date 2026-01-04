package com.stokbizde.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.stokbizde.model.CashRegister;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CashRegisterDAO {
    private final MongoCollection<Document> collection;

    public CashRegisterDAO() {
        MongoDatabase database = DatabaseUtil.getDatabase();
        this.collection = database.getCollection("cash_registers");
    }

    // Kasa kaydet
    public String save(CashRegister cashRegister) {
        Document doc = new Document()
                .append("name", cashRegister.getName())
                .append("code", cashRegister.getCode())
                .append("type", cashRegister.getType() != null ? cashRegister.getType().name() : null)
                .append("branchId", cashRegister.getBranchId())
                .append("branchName", cashRegister.getBranchName())
                .append("warehouseId", cashRegister.getWarehouseId())
                .append("warehouseName", cashRegister.getWarehouseName())
                .append("balance", cashRegister.getBalance())
                .append("currency", cashRegister.getCurrency())
                .append("active", cashRegister.isActive())
                .append("description", cashRegister.getDescription())
                .append("createdAt", cashRegister.getCreatedAt())
                .append("createdBy", cashRegister.getCreatedBy());

        if (cashRegister.getId() != null && !cashRegister.getId().isEmpty()) {
            collection.replaceOne(
                    Filters.eq("_id", new ObjectId(cashRegister.getId())),
                    doc
            );
            return cashRegister.getId();
        } else {
            collection.insertOne(doc);
            return doc.getObjectId("_id").toString();
        }
    }

    // ID'ye göre kasa bul
    public CashRegister findById(String id) {
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return doc != null ? documentToCashRegister(doc) : null;
    }

    // Tüm kasaları getir
    public List<CashRegister> findAll() {
        List<CashRegister> cashRegisters = new ArrayList<>();
        collection.find()
                .sort(Sorts.ascending("name"))
                .forEach(doc -> cashRegisters.add(documentToCashRegister(doc)));
        return cashRegisters;
    }

    // Şubeye göre kasaları getir
    public List<CashRegister> findByBranch(String branchId) {
        List<CashRegister> cashRegisters = new ArrayList<>();
        collection.find(Filters.eq("branchId", branchId))
                .sort(Sorts.ascending("name"))
                .forEach(doc -> cashRegisters.add(documentToCashRegister(doc)));
        return cashRegisters;
    }

    // Aktif kasaları getir
    public List<CashRegister> findActive() {
        List<CashRegister> cashRegisters = new ArrayList<>();
        collection.find(Filters.eq("active", true))
                .sort(Sorts.ascending("name"))
                .forEach(doc -> cashRegisters.add(documentToCashRegister(doc)));
        return cashRegisters;
    }

    // Şubeye göre aktif kasaları getir
    public List<CashRegister> findActiveByBranch(String branchId) {
        List<CashRegister> cashRegisters = new ArrayList<>();
        collection.find(Filters.and(
                        Filters.eq("branchId", branchId),
                        Filters.eq("active", true)
                ))
                .sort(Sorts.ascending("name"))
                .forEach(doc -> cashRegisters.add(documentToCashRegister(doc)));
        return cashRegisters;
    }

    // Kasa koduna göre kasa bul
    public CashRegister findByCode(String code) {
        Document doc = collection.find(Filters.eq("code", code)).first();
        return doc != null ? documentToCashRegister(doc) : null;
    }

    // Kasa sil
    public boolean delete(String id) {
        return collection.deleteOne(Filters.eq("_id", new ObjectId(id))).getDeletedCount() > 0;
    }

    // Kasa bakiyesini güncelle
    public boolean updateBalance(String id, double newBalance) {
        return collection.updateOne(
                Filters.eq("_id", new ObjectId(id)),
                new Document("$set", new Document("balance", newBalance))
        ).getModifiedCount() > 0;
    }

    // Document'i CashRegister'a çevir
    private CashRegister documentToCashRegister(Document doc) {
        CashRegister cashRegister = new CashRegister();
        cashRegister.setId(doc.getObjectId("_id").toString());
        cashRegister.setName(doc.getString("name"));
        cashRegister.setCode(doc.getString("code"));

        String typeStr = doc.getString("type");
        if (typeStr != null) {
            cashRegister.setType(CashRegister.CashRegisterType.valueOf(typeStr));
        }

        cashRegister.setBranchId(doc.getString("branchId"));
        cashRegister.setBranchName(doc.getString("branchName"));
        cashRegister.setWarehouseId(doc.getString("warehouseId"));
        cashRegister.setWarehouseName(doc.getString("warehouseName"));
        cashRegister.setBalance(doc.getDouble("balance") != null ? doc.getDouble("balance") : 0.0);
        cashRegister.setCurrency(doc.getString("currency"));
        cashRegister.setActive(doc.getBoolean("active", true));
        cashRegister.setDescription(doc.getString("description"));

        // Date'i LocalDateTime'a çevir
        Date createdAtDate = doc.getDate("createdAt");
        if (createdAtDate != null) {
            cashRegister.setCreatedAt(createdAtDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }

        cashRegister.setCreatedBy(doc.getString("createdBy"));

        return cashRegister;
    }
}

