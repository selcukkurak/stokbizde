package com.stokbizde.model;

public class Neighborhood {
    private String neighborhoodId;
    private String neighborhoodName;
    private String postalCode;

    public Neighborhood() {
    }

    public Neighborhood(String neighborhoodId, String neighborhoodName, String postalCode) {
        this.neighborhoodId = neighborhoodId;
        this.neighborhoodName = neighborhoodName;
        this.postalCode = postalCode;
    }

    public String getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(String neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public String getNeighborhoodName() {
        return neighborhoodName;
    }

    public void setNeighborhoodName(String neighborhoodName) {
        this.neighborhoodName = neighborhoodName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}

