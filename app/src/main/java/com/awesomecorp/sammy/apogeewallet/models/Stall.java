package com.awesomecorp.sammy.apogeewallet.models;

import java.util.List;

/**
 * Created by sammy on 11/2/18.
 */

public class Stall {
    boolean cancelled;
    String UID;
    String order_id;
    String stallname;
    String sgid;
    boolean order_complete;
    boolean order_ready;
    List<Sales> sales;

    public Stall() {
    }

    public Stall(boolean cancelled, String UID, String order_id, String stallname, String sgid, boolean order_complete, boolean order_ready, List<Sales> sales) {
        this.cancelled = cancelled;
        this.UID = UID;
        this.order_id = order_id;
        this.stallname = stallname;
        this.sgid = sgid;
        this.order_complete = order_complete;
        this.order_ready = order_ready;
        this.sales = sales;
    }

    public String getSgid() {
        return sgid;
    }

    public void setSgid(String sgid) {
        this.sgid = sgid;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public boolean isOrder_ready() {
        return order_ready;
    }

    public void setOrder_ready(boolean order_ready) {
        this.order_ready = order_ready;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getStallname() {
        return stallname;
    }

    public void setStallname(String stallname) {
        this.stallname = stallname;
    }

    public boolean isOrder_complete() {
        return order_complete;
    }

    public void setOrder_complete(boolean order_complete) {
        this.order_complete = order_complete;
    }

    public List<Sales> getSales() {
        return sales;
    }

    public void setSales(List<Sales> sales) {
        this.sales = sales;
    }
}
