package com.silong.Object;

public class AdminLog {

    private int id;
    private String date;
    private String time;
    private String adminEmail;
    private String log;

    public AdminLog(int id, String date, String time, String adminEmail, String log) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.adminEmail = adminEmail;
        this.log = log;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
