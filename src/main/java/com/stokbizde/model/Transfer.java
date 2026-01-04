package com.stokbizde.model;

import java.util.Date;

public class Transfer {
    private String id;
    private String fromBranchId;
    private String toBranchId;
    private String productId;
    private int quantity;
    private TransferType type;
    private TransferStatus status;
    private Date date;

    public enum TransferType {
        SALE, RETURN, ADJUSTMENT
    }

    public enum TransferStatus {
        PENDING, COMPLETED, CANCELLED
    }

    // Parametresiz constructor (MongoDB POJO Codec için gerekli)
    public Transfer() {
    }

    public Transfer(String id, String fromBranchId, String toBranchId, String productId, int quantity, TransferType type, TransferStatus status, Date date) {
        this.id = id;
        this.fromBranchId = fromBranchId;
        this.toBranchId = toBranchId;
        this.productId = productId;
        this.quantity = quantity;
        this.type = type;
        this.status = status;
        this.date = date;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFromBranchId() { return fromBranchId; }
    public void setFromBranchId(String fromBranchId) { this.fromBranchId = fromBranchId; }
    public String getToBranchId() { return toBranchId; }
    public void setToBranchId(String toBranchId) { this.toBranchId = toBranchId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public TransferType getType() { return type; }
    public void setType(TransferType type) { this.type = type; }
    public TransferStatus getStatus() { return status; }
    public void setStatus(TransferStatus status) { this.status = status; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}
