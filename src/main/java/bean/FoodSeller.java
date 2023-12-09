package bean;

public class FoodSeller {
    private String storeID;
    private String password;
    private String name;
    private double accountBalance;

    public FoodSeller() {
    }

    public FoodSeller(String storeID, String password, String name, double accountBalance) {
        this.storeID = storeID;
        this.password = password;
        this.name = name;
        this.accountBalance = accountBalance;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    @Override
    public String toString() {
        return "FoodSeller{" +
                "storeID='" + storeID + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", accountBalance=" + accountBalance +
                '}';
    }
}
