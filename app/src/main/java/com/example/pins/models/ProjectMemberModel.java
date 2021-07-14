package com.example.pins.models;

public class ProjectMemberModel {
    private String userid;
    private String firstname;
    private String lastname;
    private String email;
    private String role;
    private String imageUrl;


    public static final String ROLE_MANAGER = "Manager";
    public static final String ROLE_EMPLOYEE = "Employee";

    public ProjectMemberModel(){}

    public ProjectMemberModel(
            String userid,
            String firstname,
            String lastname,
            String email,
            String role,
            String imageUrl
    ) {
        this.userid = userid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.imageUrl = imageUrl;
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

}
