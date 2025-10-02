package com.customercare.model;

import java.time.LocalDateTime;

public class OrderStatus {
    private String orderId;
    private String status;
    private String trackingNumber;
    private LocalDateTime estimatedDelivery;
    private String customerName;
    
    public OrderStatus() {}
    
    public OrderStatus(String orderId, String status, String trackingNumber, 
                      LocalDateTime estimatedDelivery, String customerName) {
        this.orderId = orderId;
        this.status = status;
        this.trackingNumber = trackingNumber;
        this.estimatedDelivery = estimatedDelivery;
        this.customerName = customerName;
    }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    
    public LocalDateTime getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
