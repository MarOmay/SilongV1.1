package com.silong.dev;

import android.graphics.Bitmap;

public class HistoryData {

    private String genderType;
    private String estAge;
    private String petColor;
    private String estSize;
    private Bitmap petPic;
    private String adoptDate;
    private int adoptStat;

    public HistoryData(Bitmap petPic, String genderType, String estAge, String petColor, String estSize, String adoptDate, int adoptStat) {
        this.petPic = petPic;
        this.genderType = genderType;
        this.estAge = estAge;
        this.petColor = petColor;
        this.estSize = estSize;
        this.adoptDate = adoptDate;
        this.adoptStat = adoptStat;
    }

    public String getGenderType() {
        return genderType;
    }

    public void setGenderType(String genderType) {
        this.genderType = genderType;
    }

    public String getEstAge() {
        return estAge;
    }

    public void setEstAge(String estAge) {
        this.estAge = estAge;
    }

    public String getPetColor() {
        return petColor;
    }

    public void setPetColor(String petColor) {
        this.petColor = petColor;
    }

    public String getEstSize() {
        return estSize;
    }

    public void setEstSize(String estSize) {
        this.estSize = estSize;
    }

    public Bitmap getPetPic() {
        return petPic;
    }

    public void setPetPic(Bitmap petPic) {
        this.petPic = petPic;
    }

    public String getAdoptDate() {
        return adoptDate;
    }

    public void setAdoptDate(String adoptDate) {
        this.adoptDate = adoptDate;
    }

    public int getAdoptStat() {
        return adoptStat;
    }

    public void setAdoptStat(int adoptStat) {
        this.adoptStat = adoptStat;
    }

}
