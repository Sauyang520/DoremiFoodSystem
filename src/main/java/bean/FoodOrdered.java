package bean;

public class FoodOrdered {
    private String orderID;
    private Food food;
    private int quantity;
    private double amount;
    private boolean foodStatus;
    private String ReportDate;
    private String foodID;
    private String storeID;
    private String foodName;

    public FoodOrdered(String orderID, Food food, int quantity, double amount, boolean foodStatus) {
        this.orderID = orderID;
        this.food = food;
        this.quantity = quantity;
        this.amount = amount;
        this.foodStatus = foodStatus;
    }

    public FoodOrdered() {
    }

    public FoodOrdered(Food food, int quantity, double amount, boolean foodStatus) {
        this.food = food;
        this.quantity = quantity;
        this.amount = amount;
        this.foodStatus = foodStatus;
    }

    public FoodOrdered(String foodID, String foodName, String storeID) {
        this.foodID = foodID;
        this.foodName = foodName;
        this.storeID = storeID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getReportDate() {
        return ReportDate;
    }

    public void setReportDate(String reportDate) {
        ReportDate = reportDate;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isFoodStatus() {
        return foodStatus;
    }

    public void setFoodStatus(boolean foodStatus) {
        this.foodStatus = foodStatus;
    }

    @Override
    public String toString() {
        return "FoodOrdered{" +
                "food=" + food +
                ", quantity=" + quantity +
                ", amount=" + amount +
                ", foodStatus=" + foodStatus +
                '}';
    }
}
