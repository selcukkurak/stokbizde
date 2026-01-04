package com.stokbizde.model;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Province {
    private ObjectId id;
    private String provinceId;
    private String provinceName;
    private List<District> districts;

    public Province() {
        this.districts = new ArrayList<>();
    }

    public Province(String provinceId, String provinceName) {
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.districts = new ArrayList<>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    public void addDistrict(District district) {
        this.districts.add(district);
    }
}

