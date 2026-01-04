package com.stokbizde.model;

public class Branch {
    private String id;
    private String name;              // Şube Adı
    private String managerId;         // Şube Müdürü (User ID)
    private String managerName;       // Şube Müdürü Adı
    private String location;          // Konum (eski format için)
    private String city;              // Şehir (eski format için)
    private String province;          // İl
    private String district;          // İlçe
    private String neighborhood;      // Mahalle
    private String address;           // Adres
    private String phone1;            // Telefon 1
    private String phone2;            // Telefon 2
    private boolean active;           // Aktif/Pasif

    // Parametresiz constructor (MongoDB POJO Codec için gerekli)
    public Branch() {
        this.active = true;
    }

    public Branch(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.active = true;
    }

    // Getters and setters
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

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name;
    }
}
