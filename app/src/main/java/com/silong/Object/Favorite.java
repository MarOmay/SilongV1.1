package com.silong.Object;

public class Favorite {

    private int id;
    private int petID;

    public Favorite(int id, int petID) {
        this.id = id;
        this.petID = petID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPetID() {
        return petID;
    }

    public void setPetID(int petID) {
        this.petID = petID;
    }
}
