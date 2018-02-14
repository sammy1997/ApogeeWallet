package com.awesomecorp.sammy.apogeewallet.models;

import java.util.Date;
import java.util.List;

/**
 * Created by sammy on 8/2/18.
 */

public class Transaction {
    String created_at;
    long id ;
    String t_type;
    long value;
    Stall stallgroup;
    Date date;
    Transfer transfer;

    public Transaction(String created_at, long id, String t_type, long value, Stall stallgroup, Date date, Transfer transfer) {
        this.created_at = created_at;
        this.id = id;
        this.t_type = t_type;
        this.value = value;
        this.stallgroup = stallgroup;
        this.date = date;
        this.transfer = transfer;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Transaction() {
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getT_type() {
        return t_type;
    }

    public void setT_type(String t_type) {
        this.t_type = t_type;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public Stall getStallgroup() {
        return stallgroup;
    }

    public void setStallgroup(Stall stallgroup) {
        this.stallgroup = stallgroup;
    }
}
