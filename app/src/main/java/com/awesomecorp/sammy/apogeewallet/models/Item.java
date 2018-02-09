package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 9/2/18.
 */

public class Item {
    String itemName;
    String cost;

    public Item() {
    }

    public Item(String itemName, String cost) {
        this.itemName = itemName;
        this.cost = cost;
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
