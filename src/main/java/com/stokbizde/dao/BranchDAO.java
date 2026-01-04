package com.stokbizde.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.stokbizde.model.Branch;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class BranchDAO {
    private MongoCollection<Branch> collection;

    public BranchDAO() {
        MongoDatabase database = DatabaseUtil.getDatabase();
        this.collection = database.getCollection("branches", Branch.class);
    }

    public void addBranch(Branch branch) {
        if (branch.getId() == null) {
            branch.setId(new ObjectId().toString());
        }
        collection.insertOne(branch);
    }

    public void insertBranch(Branch branch) {
        if (branch.getId() == null || branch.getId().isEmpty()) {
            branch.setId(new ObjectId().toString());
        }
        collection.insertOne(branch);
    }

    public List<Branch> getAllBranches() {
        List<Branch> branches = new ArrayList<>();
        collection.find().into(branches);
        return branches;
    }

    public List<Branch> findAll() {
        return getAllBranches();
    }

    public void updateBranch(Branch branch) {
        collection.replaceOne(eq("_id", branch.getId()), branch);
    }

    public void deleteBranch(String id) {
        collection.deleteOne(eq("_id", id));
    }

    public Branch getBranchById(String branchId) {
        return collection.find(eq("_id", branchId)).first();
    }
}
