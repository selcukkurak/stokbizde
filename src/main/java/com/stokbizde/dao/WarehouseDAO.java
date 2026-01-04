package com.stokbizde.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.stokbizde.model.Warehouse;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class WarehouseDAO {
    private final MongoCollection<Document> collection;

    public WarehouseDAO() {
        MongoDatabase db = DatabaseUtil.getDatabase();
        collection = db.getCollection("warehouses");
    }

    public List<Warehouse> getAllWarehouses() {
        List<Warehouse> warehouses = new ArrayList<>();
        for (Document doc : collection.find()) {
            warehouses.add(documentToWarehouse(doc));
        }
        return warehouses;
    }

    public List<Warehouse> getActiveWarehouses() {
        List<Warehouse> warehouses = new ArrayList<>();
        for (Document doc : collection.find(eq("active", true))) {
            warehouses.add(documentToWarehouse(doc));
        }
        return warehouses;
    }

    public List<Warehouse> findByBranch(String branchId) {
        List<Warehouse> warehouses = new ArrayList<>();
        for (Document doc : collection.find(eq("branchId", branchId))) {
            warehouses.add(documentToWarehouse(doc));
        }
        return warehouses;
    }

    public Warehouse getWarehouseById(String id) {
        Document doc = collection.find(eq("_id", new ObjectId(id))).first();
        return doc != null ? documentToWarehouse(doc) : null;
    }

    public void insertWarehouse(Warehouse warehouse) {
        Document doc = warehouseToDocument(warehouse);
        collection.insertOne(doc);
        warehouse.setId(doc.getObjectId("_id").toString());
    }

    public void updateWarehouse(Warehouse warehouse) {
        Document doc = warehouseToDocument(warehouse);
        collection.replaceOne(eq("_id", new ObjectId(warehouse.getId())), doc);
    }

    public void deleteWarehouse(String id) {
        collection.deleteOne(eq("_id", new ObjectId(id)));
    }

    private Document warehouseToDocument(Warehouse warehouse) {
        Document doc = new Document();
        if (warehouse.getId() != null && !warehouse.getId().isEmpty()) {
            doc.append("_id", new ObjectId(warehouse.getId()));
        }
        doc.append("name", warehouse.getName())
           .append("branchId", warehouse.getBranchId())
           .append("branchName", warehouse.getBranchName())
           .append("responsibleId", warehouse.getResponsibleId())
           .append("responsibleName", warehouse.getResponsibleName())
           .append("city", warehouse.getCity())
           .append("address", warehouse.getAddress())
           .append("phone1", warehouse.getPhone1())
           .append("phone2", warehouse.getPhone2())
           .append("active", warehouse.isActive());
        return doc;
    }

    private Warehouse documentToWarehouse(Document doc) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(doc.getObjectId("_id").toString());
        warehouse.setName(doc.getString("name"));
        warehouse.setBranchId(doc.getString("branchId"));
        warehouse.setBranchName(doc.getString("branchName"));
        warehouse.setResponsibleId(doc.getString("responsibleId"));
        warehouse.setResponsibleName(doc.getString("responsibleName"));
        warehouse.setCity(doc.getString("city"));
        warehouse.setAddress(doc.getString("address"));
        warehouse.setPhone1(doc.getString("phone1"));
        warehouse.setPhone2(doc.getString("phone2"));
        warehouse.setActive(doc.getBoolean("active", true));
        return warehouse;
    }
}

