package bean;

public class DeliveryMan {
    private String deliveryManID;
    private String password;
    private String name;
    private double accountBalance;

    public DeliveryMan() {
    }

    public DeliveryMan(String deliveryManID, String password, String name, double accountBalance) {
        this.deliveryManID = deliveryManID;
        this.password = password;
        this.name = name;
        this.accountBalance = accountBalance;
    }

    public String getDeliveryManID() {
        return deliveryManID;
    }

    public void setDeliveryManID(String deliveryManID) {
        this.deliveryManID = deliveryManID;
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
        return "DeliveryMan{" +
                "deliveryManID='" + deliveryManID + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", accountBalance=" + accountBalance +
                '}';
    }
}
