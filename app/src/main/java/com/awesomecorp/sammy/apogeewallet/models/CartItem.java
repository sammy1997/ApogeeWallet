package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 10/2/18.
 */

public class CartItem {
    String quantity;
    String id;
    String name;
    String price;

    public CartItem(String quantity, String id, String name, String price) {
        this.quantity = quantity;
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public CartItem() {
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
