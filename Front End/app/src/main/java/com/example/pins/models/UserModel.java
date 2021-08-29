package com.example.pins.models;

import android.net.Uri;

import java.util.List;

public class UserModel {

    private String userid;
    private String firstname;
    private String lastname;
    private String email;
    private String role;
    private String imageUrl;
    private String currentProjectId;
    private List<String> allProjects;

    public static final String ROLE_MANAGER = "Manager";
    public static final String ROLE_EMPLOYEE = "Employee";

    private static UserModel userInstance;

    public UserModel(){}

    public UserModel(
            String userid,
            String firstname,
            String lastname,
            String email,
            String role,
            String imageUrl,
            String currentProjectId,
            List<String> allProjects
            ) {
        this.userid = userid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.imageUrl = imageUrl;
        this.currentProjectId = currentProjectId;
        this.allProjects = allProjects;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCurrentProjectId() {
        return currentProjectId;
    }

    public void setCurrentProjectId(String currentProjectId) {
        this.currentProjectId = currentProjectId;
    }

    public List<String> getAllProjects() {
        return allProjects;
    }

    public void setAllProjects(List<String> allProjects) {
        this.allProjects = allProjects;
    }
}
