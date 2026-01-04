package com.stokbizde.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.stokbizde.model.Province;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class LocationDAO {
    private final MongoCollection<Province> provinceCollection;
    private final MongoCollection<Document> metadataCollection;

    public LocationDAO() {
        MongoDatabase database = DatabaseUtil.getDatabase();
        this.provinceCollection = database.getCollection("provinces", Province.class);
        this.metadataCollection = database.getCollection("location_metadata");
    }

    /**
     * Veritabanında adres verileri var mı kontrol eder
     */
    public boolean isAddressDataInitialized() {
        try {
            // Önce metadata koleksiyonunu kontrol et
            Document metadata = metadataCollection.find(eq("key", "address_data_initialized")).first();

            if (metadata != null && metadata.getBoolean("value", false)) {
                // Metadata varsa, gerçekten veri de var mı kontrol et
                long provinceCount = provinceCollection.countDocuments();

                if (provinceCount > 0) {
                    System.out.println("→ Veritabanında " + provinceCount + " il kaydı bulundu.");
                    return true;
                }
            }

            // Metadata yoksa veya il verisi yoksa false döndür
            return false;

        } catch (Exception e) {
            System.err.println("Veritabanı kontrol hatası: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adres verilerini veritabanına kaydeder
     */
    public void saveAllProvinces(List<Province> provinces) {
        // Önce mevcut verileri temizle
        provinceCollection.drop();

        // Yeni verileri kaydet
        if (!provinces.isEmpty()) {
            provinceCollection.insertMany(provinces);
        }

        // Metadata'yı güncelle
        Document metadata = new Document("key", "address_data_initialized")
                .append("value", true)
                .append("timestamp", System.currentTimeMillis())
                .append("provinceCount", provinces.size());

        metadataCollection.deleteOne(eq("key", "address_data_initialized"));
        metadataCollection.insertOne(metadata);

        System.out.println("Toplam " + provinces.size() + " il verisi veritabanına kaydedildi.");
    }

    /**
     * Tüm illeri getirir
     */
    public List<Province> getAllProvinces() {
        return provinceCollection.find().into(new java.util.ArrayList<>());
    }

    /**
     * İl adına göre il getirir
     */
    public Province getProvinceByName(String provinceName) {
        return provinceCollection.find(eq("provinceName", provinceName)).first();
    }

    /**
     * İl ID'sine göre il getirir
     */
    public Province getProvinceById(String provinceId) {
        return provinceCollection.find(eq("provinceId", provinceId)).first();
    }
}

