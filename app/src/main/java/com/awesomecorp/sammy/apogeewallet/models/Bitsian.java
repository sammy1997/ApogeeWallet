package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 11/2/18.
 */

public class Bitsian {
    long id;
    String long_id;
    String name;
    String email;
    String gender;
    String barcode;
    String bhawan;
    long room_no;

    public Bitsian() {
    }

    public Bitsian(long id, String long_id, String name, String email, String gender, String barcode, String bhawan, long room_no) {
        this.id = id;
        this.long_id = long_id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.barcode = barcode;
        this.bhawan = bhawan;
        this.room_no = room_no;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLong_id() {
        return long_id;
    }

    public void setLong_id(String long_id) {
        this.long_id = long_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBhawan() {
        return bhawan;
    }

    public void setBhawan(String bhawan) {
        this.bhawan = bhawan;
    }

    public long getRoom_no() {
        return room_no;
    }

    public void setRoom_no(long room_no) {
        this.room_no = room_no;
    }
}
