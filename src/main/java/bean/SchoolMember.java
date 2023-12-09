package bean;

public class SchoolMember {
    private String tpNumber;
    private String password;
    private String name;
    private double accountBalance;

    public SchoolMember() {
    }

    public SchoolMember(String tpNumber, String password, String name, double accountBalance) {
        this.tpNumber = tpNumber;
        this.password = password;
        this.name = name;
        this.accountBalance = accountBalance;
    }

    public String getTpNumber() {
        return tpNumber;
    }

    public void setTpNumber(String tpNumber) {
        this.tpNumber = tpNumber;
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
        return "SchoolMember{" +
                "tpNumber='" + tpNumber + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", accountBalance=" + accountBalance +
                '}';
    }
}
