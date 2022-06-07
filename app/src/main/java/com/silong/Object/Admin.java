package com.silong.Object;

public class Admin {

    private String adminID;
    private String adminEmail;
    private String firstName;
    private String lastName;
    private boolean accountStatus;
    private String [] userInteraction; //emails of users

    public Admin(String adminID, String adminEmail, String firstName, String lastName, boolean accountStatus, String[] userInteraction) {
        this.adminID = adminID;
        this.adminEmail = adminEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountStatus = accountStatus;
        this.userInteraction = userInteraction;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(boolean accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String[] getUserInteraction() {
        return userInteraction;
    }

    public void setUserInteraction(String[] userInteraction) {
        this.userInteraction = userInteraction;
    }
}
