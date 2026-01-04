package com.stokbizde.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.stokbizde.model.Product;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class ProductDAO {
    private MongoCollection<Product> collection;

    public ProductDAO() {
        MongoDatabase database = DatabaseUtil.getDatabase();
        this.collection = database.getCollection("products", Product.class);
    }

    public void addProduct(Product product) {
        if (product.getId() == null) {
            product.setId(new ObjectId().toString());
        }
        collection.insertOne(product);
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        collection.find().into(products);
        return products;
    }

    public void updateProduct(Product product) {
        collection.replaceOne(eq("_id", product.getId()), product);
    }

    public void deleteProduct(String id) {
        collection.deleteOne(eq("_id", id));
    }
}
