package com.example.pins.models;

public class UserModel {

    String userid,firstname,lastname,email;

    private static UserModel userInstance;

    public UserModel(){}

    public UserModel(String userid, String firstname, String lastname, String email) {
        this.userid = userid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public static UserModel getUserInstance() {
        if(userInstance == null) {
            userInstance = new UserModel();
        }

        return userInstance;
    }

    public void setUserInstance(UserModel instance) {
        UserModel.userInstance = instance;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
