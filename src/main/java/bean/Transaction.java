package bean;

import java.time.LocalDateTime;
import java.util.List;

public class Transaction {
    private String orderID;
    private String tpNumber;
    private double total;
    private String method;
    private double deliveryFee;
    private String deliveryManID;
    private String deliveryAddress;
    private boolean orderStatus;
    private LocalDateTime receiptGeneratedTime;
    private List<FoodOrdered> foodOrdered;


    public Transaction() {
    }

    public Transaction(String orderID, String tpNumber, double total, LocalDateTime receiptGeneratedTime, String method, String deliveryManID, double deliveryFee, String deliveryAddress, List<FoodOrdered> foodOrdered, boolean orderStatus) {
        this.orderID = orderID;
        this.tpNumber = tpNumber;
        this.total = total;
        this.method = method;
        this.deliveryFee = deliveryFee;
        this.deliveryManID = deliveryManID;
        this.orderStatus = orderStatus;
        this.receiptGeneratedTime = receiptGeneratedTime;
        this.foodOrdered = foodOrdered;
        this.deliveryAddress = deliveryAddress;
    }

    public Transaction(String orderID, String tpNumber, double total, String method, LocalDateTime receiptGeneratedTime) {
        this.orderID = orderID;
        this.tpNumber = tpNumber;
        this.total = total;
        this.method = method;
        this.receiptGeneratedTime = receiptGeneratedTime;
    }

    public Transaction(String orderID, String deliveryAddress) {
        this.orderID = orderID;
        this.deliveryAddress = deliveryAddress;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getTpNumber() {
        return tpNumber;
    }

    public void setTpNumber(String tpNumber) {
        this.tpNumber = tpNumber;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getDeliveryManID() {
        return deliveryManID;
    }

    public void setDeliveryManID(String deliveryManID) {
        this.deliveryManID = deliveryManID;
    }

    public boolean isOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(boolean orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getReceiptGeneratedTime() {
        return receiptGeneratedTime;
    }

    public void setReceiptGeneratedTime(LocalDateTime receiptGeneratedTime) {
        this.receiptGeneratedTime = receiptGeneratedTime;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<FoodOrdered> getFoodOrdered() {
        return foodOrdered;
    }

    public void setFoodOrdered(List<FoodOrdered> foodOrdered) {
        this.foodOrdered = foodOrdered;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "orderID='" + orderID + '\'' +
                ", tpNumber='" + tpNumber + '\'' +
                ", total=" + total +
                ", method='" + method + '\'' +
                ", deliveryFee=" + deliveryFee +
                ", deliveryManID='" + deliveryManID + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", orderStatus=" + orderStatus +
                ", localDateTime=" + receiptGeneratedTime +
                ", foodOrdered=" + foodOrdered +
                '}';
    }
}
