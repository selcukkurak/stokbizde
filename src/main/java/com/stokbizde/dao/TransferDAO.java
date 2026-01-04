package com.stokbizde.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.stokbizde.model.Transfer;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class TransferDAO {
    private MongoCollection<Transfer> collection;

    public TransferDAO() {
        MongoDatabase database = DatabaseUtil.getDatabase();
        this.collection = database.getCollection("transfers", Transfer.class);
    }

    public void addTransfer(Transfer transfer) {
        if (transfer.getId() == null) {
            transfer.setId(new ObjectId().toString());
        }
        collection.insertOne(transfer);
    }

    public List<Transfer> getAllTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        collection.find().into(transfers);
        return transfers;
    }

    public void updateTransferStatus(String id, Transfer.TransferStatus status) {
        collection.updateOne(eq("_id", id), set("status", status));
    }
}
