package bean;

import java.time.LocalDateTime;

public class BalanceHistory {

    private String tpNumber;
    private int transactionNumber;
    private LocalDateTime dateTime;
    private double changeAmount;
    private double newBalance;
    private String action;

    public BalanceHistory() {
    }

    public BalanceHistory(String tpNumber, int transactionNumber, LocalDateTime dateTime, double changeAmount, double newBalance, String action) {
        this.tpNumber = tpNumber;
        this.transactionNumber = transactionNumber;
        this.dateTime = dateTime;
        this.changeAmount = changeAmount;
        this.newBalance = newBalance;
        this.action = action;
    }

    public String getTpNumber() {
        return tpNumber;
    }

    public void setTpNumber(String tpNumber) {
        this.tpNumber = tpNumber;
    }

    public int getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(int transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "BalanceHistory{" +
                "tpNumber='" + tpNumber + '\'' +
                ", transactionNumber=" + transactionNumber +
                ", dateTime=" + dateTime +
                ", changeAmount=" + changeAmount +
                ", newBalance=" + newBalance +
                ", action='" + action + '\'' +
                '}';
    }
}
