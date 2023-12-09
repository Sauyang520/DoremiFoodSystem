package bean;

public class Food {
    private FoodSeller foodSeller;
    private String foodID;
    private String foodName;
    private double foodPrice;
    private String foodDescription;
    private String imagePath;

    public Food() {
    }

    public Food(String foodID, String foodName, double foodPrice, String foodDescription) {
        this.foodID = foodID;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDescription = foodDescription;
    }

    public Food(String foodID, String foodName) {
        this.foodID = foodID;
        this.foodName = foodName;
    }

    public Food(String foodID, String foodName, double foodPrice, String foodDescription, String imagePath) {
        this.foodID = foodID;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDescription = foodDescription;
        this.imagePath = imagePath;
    }

    public Food(FoodSeller foodSeller, String foodID, String foodName, double foodPrice, String foodDescription, String imagePath) {
        this.foodSeller = foodSeller;
        this.foodID = foodID;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDescription = foodDescription;
        this.imagePath = imagePath;
    }

    public FoodSeller getFoodSeller() {
        return foodSeller;
    }

    public void setFoodSeller(FoodSeller foodSeller) {
        this.foodSeller = foodSeller;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Food{" +
                "storeID='" + foodSeller + '\'' +
                ", foodID='" + foodID + '\'' +
                ", foodName='" + foodName + '\'' +
                ", foodPrice='" + foodPrice + '\'' +
                ", foodDescription='" + foodDescription + '\'' +
                '}';
    }
}
