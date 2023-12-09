// Programmer Name: Mr.Sim Sau Yang
// Program Name: DataHolder
// First Written: Saturday, 17-June-2023

package utils;

import bean.*;

import java.util.List;

/**
 * class DataHolder is a Singleton type class
 * Used to transform data from a page to another page
 */
public class DataHolder {

    private static DataHolder instance;
    private SchoolMember schoolMember;
    private FoodSeller foodSeller;
    private DeliveryMan deliveryMan;
    private SchoolAdmin schoolAdmin;
    private Transaction transaction;
    private Food food;
    private List<FoodOrdered> foodOrdered;
    private String foodMethod;
    private String deliveryAddress;
    private double deliveryFee;

    private DataHolder() {
    }

    public static synchronized DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    public static void deleteInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    public SchoolMember getSchoolMember() {
        return schoolMember;
    }

    public void setSchoolMember(SchoolMember schoolMember) {
        this.schoolMember = schoolMember;
    }

    public FoodSeller getFoodSeller() {
        return foodSeller;
    }

    public void setFoodSeller(FoodSeller foodSeller) {
        this.foodSeller = foodSeller;
    }

    public DeliveryMan getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(DeliveryMan deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public SchoolAdmin getSchoolAdmin() {
        return schoolAdmin;
    }

    public void setSchoolAdmin(SchoolAdmin schoolAdmin) {
        this.schoolAdmin = schoolAdmin;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public List<FoodOrdered> getFoodOrdered() {
        return foodOrdered;
    }

    public void setFoodOrdered(List<FoodOrdered> foodOrdered) {
        this.foodOrdered = foodOrdered;
    }

    public String getFoodMethod() {
        return foodMethod;
    }

    public void setFoodMethod(String foodMethod) {
        this.foodMethod = foodMethod;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
}
