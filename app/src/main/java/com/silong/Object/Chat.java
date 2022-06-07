package com.silong.Object;

public class Chat {

    private int id;
    private String adminEmail;
    private String date;
    private String time;
    private String content;

    public Chat() {
    }

    public Chat(int id, String adminEmail, String date, String time, String content) {
        this.id = id;
        this.adminEmail = adminEmail;
        this.date = date;
        this.time = time;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
