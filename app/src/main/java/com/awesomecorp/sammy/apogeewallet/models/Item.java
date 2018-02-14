package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 9/2/18.
 */

public class Item {
    String itemName;
    String cost;
    String id;
    boolean is_available;
    String size;
    String color;

    public Item() {
    }

    public Item(String itemName, String cost, String id, boolean is_available, String size, String color) {
        this.itemName = itemName;
        this.cost = cost;
        this.id = id;
        this.is_available = is_available;
        this.size = size;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIs_available() {
        return is_available;
    }

    public void setIs_available(boolean is_available) {
        this.is_available = is_available;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
