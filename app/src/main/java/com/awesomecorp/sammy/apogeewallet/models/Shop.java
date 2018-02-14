package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 9/2/18.
 */

public class Shop {
    String name;
    String description;
    String id;

    public Shop(String name, String description, String id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public Shop() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
