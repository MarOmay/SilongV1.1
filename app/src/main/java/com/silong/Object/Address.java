package com.silong.Object;

import java.io.Serializable;

public class Address implements Serializable {

    private String addressLine;
    private String barangay;
    private String municipality;
    private String province;
    private int zipcode;

    public Address(){}

    public Address(String addressLine, String barangay, String municipality, String province, int zipcode) {
        this.addressLine = addressLine;
        this.barangay = barangay;
        this.municipality = municipality;
        this.province = province;
        this.zipcode = zipcode;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }
}
