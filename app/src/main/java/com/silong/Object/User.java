package com.silong.Object;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class User {

    public String userID;
    public String email;
    public String firstName;
    public String lastName;
    public int gender;
    public Bitmap photo;
    public boolean accountStatus;
    public int adoptionCounter;
    public Address address;
    public ArrayList<Adoption> adoptionHistory;
    public ArrayList<Chat> chatHistory;
    public ArrayList<Favorite> likedPet;

    public User() {
    }

    public User(String userID, String email, String firstName, String lastName, int gender, Bitmap photo, boolean accountStatus, int adoptionCounter, Address address, ArrayList<Adoption> adoptionHistory, ArrayList<Chat> chatHistory, ArrayList<Favorite> likedPet) {
        this.userID = userID;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public ArrayList<Adoption> getAdoptionHistory() {
        return adoptionHistory;
    }

    public void setAdoptionHistory(ArrayList<Adoption> adoptionHistory) {
        this.adoptionHistory = adoptionHistory;
    }

    public ArrayList<Chat> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(ArrayList<Chat> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public ArrayList<Favorite> getLikedPet() {
        return likedPet;
    }

    public void setLikedPet(ArrayList<Favorite> likedPet) {
        this.likedPet = likedPet;
    }
}
