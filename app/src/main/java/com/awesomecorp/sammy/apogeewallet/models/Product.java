package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 11/2/18.
 */

public class Product {
    String colour;
    long id;
    boolean is_available;
    String name;
    String p_type;
    long price;
    String size;

    public Product() {
    }

    public Product(String colour, long id, boolean is_available, String name, String p_type, long price, String size) {
        this.colour = colour;
        this.id = id;
        this.is_available = is_available;
        this.name = name;
        this.p_type = p_type;
        this.price = price;
        this.size = size;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isIs_available() {
        return is_available;
    }

    public void setIs_available(boolean is_available) {
        this.is_available = is_available;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getP_type() {
        return p_type;
    }

    public void setP_type(String p_type) {
        this.p_type = p_type;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
