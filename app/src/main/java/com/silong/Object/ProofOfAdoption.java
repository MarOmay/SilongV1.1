package com.silong.Object;

import android.graphics.Bitmap;

public class ProofOfAdoption {

    private String genderType;
    private String dateOfAdoption;
    private Bitmap petPhoto;
    private Bitmap proofOfAdoption;

    public ProofOfAdoption() {
    }

    public String getGenderType() {
        return genderType;
    }

    public void setGenderType(String genderType) {
        this.genderType = genderType;
    }

    public String getDateOfAdoption() {
        return dateOfAdoption;
    }

    public void setDateOfAdoption(String dateOfAdoption) {
        this.dateOfAdoption = dateOfAdoption;
    }

    public Bitmap getPetPhoto() {
        return petPhoto;
    }

    public void setPetPhoto(Bitmap petPhoto) {
        this.petPhoto = petPhoto;
    }

    public Bitmap getProofOfAdoption() {
        return proofOfAdoption;
    }

    public void setProofOfAdoption(Bitmap proofOfAdoption) {
        this.proofOfAdoption = proofOfAdoption;
    }
}
