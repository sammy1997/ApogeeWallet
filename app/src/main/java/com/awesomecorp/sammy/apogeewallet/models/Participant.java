package com.awesomecorp.sammy.apogeewallet.models;

/**
 * Created by sammy on 11/2/18.
 */

public class Participant {
    String college_name;
    String email;
    String gender;
    long id;
    long phone;
    String name;
    String state;

    public Participant() {
    }

    public Participant(String college_name, String email, String gender, long id, long phone, String name, String state) {
        this.college_name = college_name;
        this.email = email;
        this.gender = gender;
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.state = state;
    }

    public String getCollege_name() {
        return college_name;
    }

    public void setCollege_name(String college_name) {
        this.college_name = college_name;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
