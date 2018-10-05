package com.example.akera.mwogo.UtilityClasses;

/**
 * Created by Apoorva on 10-May-18.
 */

public class pojoUser {

    private String username, email, name, contact, dob, gender;

    public pojoUser(){

    }

    public pojoUser(String username, String email, String name, String contact, String dob, String gender) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.contact = contact;
        this.dob = dob;
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
