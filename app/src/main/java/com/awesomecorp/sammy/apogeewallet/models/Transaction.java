package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 8/2/18.
 */

public class Transaction {
    String stallName;
    String cost;
    String dateOfTransaction;

    public Transaction() {
    }

    public Transaction(String stallName, String cost, String dateOfTransaction) {
        this.stallName = stallName;
        this.cost = cost;
        this.dateOfTransaction = dateOfTransaction;
    }

    public String getStallName() {
        return stallName;
    }

    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDateOfTransaction() {
        return dateOfTransaction;
    }

    public void setDateOfTransaction(String dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }
}
