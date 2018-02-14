package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 11/2/18.
 */

public class BillItem {
    String itemName;
    String cost;

    public BillItem(String itemName, String cost) {
        this.itemName = itemName;
        this.cost = cost;
    }

    public BillItem() {
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
