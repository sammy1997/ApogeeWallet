package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 9/2/18.
 */

public class Shop {
    String name;
    String description;

    public Shop() {
    }

    public Shop(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
