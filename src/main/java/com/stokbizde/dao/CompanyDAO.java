package com.stokbizde.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.stokbizde.model.Company;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class CompanyDAO {
    private static final String COMPANY_ID = "MAIN_COMPANY";
    private final MongoCollection<Document> collection;

    public CompanyDAO() {
        MongoDatabase db = DatabaseUtil.getDatabase();
        collection = db.getCollection("company");
    }

    /**
     * Şirket bilgilerini getirir. Yoksa yeni bir tane oluşturur.
     */
    public Company getCompanyInfo() {
        Document doc = collection.find(eq("_id", COMPANY_ID)).first();
        if (doc == null) {
            Company company = new Company();
            company.setId(COMPANY_ID);
            company.setInitialized(false);
            insertCompany(company);
            return company;
        }
        return documentToCompany(doc);
    }

    /**
     * Şirket bilgilerini günceller
     */
    public void updateCompanyInfo(Company company) {
        company.setId(COMPANY_ID);
        Document doc = companyToDocument(company);
        collection.replaceOne(eq("_id", COMPANY_ID), doc);
    }

    /**
     * Şirket bilgileri ilk kurulum yapıldı mı?
     */
    public boolean isInitialized() {
        Company company = getCompanyInfo();
        return company != null && company.isInitialized();
    }

    /**
     * İlk kurulumu tamamla
     */
    public void markAsInitialized(Company company) {
        company.setInitialized(true);
        updateCompanyInfo(company);
    }

    private void insertCompany(Company company) {
        Document doc = companyToDocument(company);
        collection.insertOne(doc);
    }

    private Document companyToDocument(Company company) {
        Document doc = new Document("_id", company.getId())
            .append("name", company.getName())
            .append("activeCompany", company.getActiveCompany())
            .append("activeWarehouse", company.getActiveWarehouse())
            .append("address", company.getAddress())
            .append("district", company.getDistrict())
            .append("city", company.getCity())
            .append("phone1", company.getPhone1())
            .append("phone2", company.getPhone2())
            .append("email", company.getEmail())
            .append("website", company.getWebsite())
            .append("logoPath", company.getLogoPath())
            .append("initialized", company.isInitialized());

        if (company.getLogoImage() != null) {
            doc.append("logoImage", company.getLogoImage());
        }

        return doc;
    }

    private Company documentToCompany(Document doc) {
        Company company = new Company();
        company.setId(doc.getString("_id"));
        company.setName(doc.getString("name"));
        company.setActiveCompany(doc.getString("activeCompany"));
        company.setActiveWarehouse(doc.getString("activeWarehouse"));
        company.setAddress(doc.getString("address"));
        company.setDistrict(doc.getString("district"));
        company.setCity(doc.getString("city"));
        company.setPhone1(doc.getString("phone1"));
        company.setPhone2(doc.getString("phone2"));
        company.setEmail(doc.getString("email"));
        company.setWebsite(doc.getString("website"));
        company.setLogoPath(doc.getString("logoPath"));
        company.setInitialized(doc.getBoolean("initialized", false));

        // Logo binary data
        Object logoImage = doc.get("logoImage");
        if (logoImage instanceof byte[]) {
            company.setLogoImage((byte[]) logoImage);
        } else if (logoImage instanceof org.bson.types.Binary) {
            company.setLogoImage(((org.bson.types.Binary) logoImage).getData());
        }

        return company;
    }
}

