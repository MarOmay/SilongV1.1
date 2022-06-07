package com.silong.Object;

public class Pet {

    private int id;
    private int status;
    private int type;
    private int gender;
    private String color;
    private int age;
    private int size;
    private int likes;

    public Pet() {
    }

    public Pet(int id, int status, int type, int gender, String color, int age, int size, int likes) {
        this.id = id;
        this.status = status;
        this.type = type;
        this.gender = gender;
        this.color = color;
        this.age = age;
        this.size = size;
        this.likes = likes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
