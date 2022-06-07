package com.silong.Object;

public class Request {

    private int id;
    private String email;
    private boolean status;
    private int requestCode;
    private String requestDetails;
    private String adminEmail;

    public Request(int id, String email, boolean status, int requestCode, String requestDetails, String adminEmail) {
        this.id = id;
        this.email = email;
        this.status = status;
        this.requestCode = requestCode;
        this.requestDetails = requestDetails;
        this.adminEmail = adminEmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getRequestDetails() {
        return requestDetails;
    }

    public void setRequestDetails(String requestDetails) {
        this.requestDetails = requestDetails;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
}
