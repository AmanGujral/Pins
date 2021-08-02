package com.example.pins.models;

public class ProjectModel {

    private String projectId;           // Project ID
    private String projectName;         // Project Name
    private String projectCode;         // Project Code
    private String managerName;         // Project Manager Name
    private String managerEmail;        // Project Manager Email
    private String AuthorizationCode;

    ProjectModel(){};

    ProjectModel(
            String projectId,
            String projectName,
            String projectCode,
            String managerName,
            String managerEmail,
            String AuthorizationCode
            ) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectCode = projectCode;
        this.managerName = managerName;
        this.managerEmail = managerEmail;
        this.AuthorizationCode=AuthorizationCode;
    }

    public String getAuthorizationCode() {
        return AuthorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
       this.AuthorizationCode = authorizationCode;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }
}
