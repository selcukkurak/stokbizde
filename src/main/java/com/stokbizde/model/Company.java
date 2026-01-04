package com.stokbizde.model;

import java.io.Serializable;

public class Company implements Serializable {
    private String id;
    private String name;           // Mağaza Adı
    private String activeCompany;  // Aktif Şirket
    private String activeWarehouse; // Aktif Depo
    private String address;        // Adres
    private String district;       // Semt
    private String city;           // Şehir
    private String phone1;         // Telefon 1
    private String phone2;         // Telefon 2
    private String email;          // E-Posta
    private String website;        // Web
    private String logoPath;       // Logo yolu
    private byte[] logoImage;      // Logo resmi (binary)
    private boolean initialized;   // İlk kurulum yapıldı mı?

    public Company() {
        this.initialized = false;
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

    public String getActiveCompany() {
        return activeCompany;
    }

    public void setActiveCompany(String activeCompany) {
        this.activeCompany = activeCompany;
    }

    public String getActiveWarehouse() {
        return activeWarehouse;
    }

    public void setActiveWarehouse(String activeWarehouse) {
        this.activeWarehouse = activeWarehouse;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public byte[] getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(byte[] logoImage) {
        this.logoImage = logoImage;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}

