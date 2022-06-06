package com.silong.dev;

import android.graphics.Bitmap;

public class User {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private int gender;
    private Bitmap photo;
    private boolean accountStatus;
    private int adoptionCounter;
    private Address address;
    private Adoption [] adoptionHistory;
    private Chat [] chatHistory;
    private Favorite[] likedPet;

    public User(String id, String email, String firstName, String lastName, int gender, Bitmap photo, boolean accountStatus, int adoptionCounter, Address address, Adoption[] adoptionHistory, Chat[] chatHistory, Favorite[] likedPet) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.photo = photo;
        this.accountStatus = accountStatus;
        this.adoptionCounter = adoptionCounter;
        this.address = address;
        this.adoptionHistory = adoptionHistory;
        this.chatHistory = chatHistory;
        this.likedPet = likedPet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public boolean isAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(boolean accountStatus) {
        this.accountStatus = accountStatus;
    }

    public int getAdoptionCounter() {
        return adoptionCounter;
    }

    public void setAdoptionCounter(int adoptionCounter) {
        this.adoptionCounter = adoptionCounter;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Adoption[] getAdoptionHistory() {
        return adoptionHistory;
    }

    public void setAdoptionHistory(Adoption[] adoptionHistory) {
        this.adoptionHistory = adoptionHistory;
    }

    public Chat[] getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(Chat[] chatHistory) {
        this.chatHistory = chatHistory;
    }

    public Favorite[] getLikedPet() {
        return likedPet;
    }

    public void setLikedPet(Favorite[] likedPet) {
        this.likedPet = likedPet;
    }
}
