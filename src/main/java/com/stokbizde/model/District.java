package com.stokbizde.model;

import java.util.ArrayList;
import java.util.List;

public class District {
    private String districtId;
    private String districtName;
    private List<Neighborhood> neighborhoods;

    public District() {
        this.neighborhoods = new ArrayList<>();
    }

    public District(String districtId, String districtName) {
        this.districtId = districtId;
        this.districtName = districtName;
        this.neighborhoods = new ArrayList<>();
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public List<Neighborhood> getNeighborhoods() {
        return neighborhoods;
    }

    public void setNeighborhoods(List<Neighborhood> neighborhoods) {
        this.neighborhoods = neighborhoods;
    }

    public void addNeighborhood(Neighborhood neighborhood) {
        this.neighborhoods.add(neighborhood);
    }
}

