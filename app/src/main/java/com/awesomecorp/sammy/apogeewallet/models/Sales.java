package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 11/2/18.
 */

public class Sales {
    long id;
    boolean paid;
    Product product;
    long quantity;

    public Sales() {
    }

    public Sales(long id, boolean paid, Product product, long quantity) {
        this.id = id;
        this.paid = paid;
        this.product = product;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
