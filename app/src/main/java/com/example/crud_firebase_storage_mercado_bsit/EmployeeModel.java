package com.example.crud_firebase_storage_mercado_bsit;

public class EmployeeModel {
    private String UID;
    private String name;
    private String email;
    private String address;
    private String number;
    private String imgURL;

    public EmployeeModel(String UID, String name, String email, String address, String number, String imgURL) {
        this.UID = UID;
        this.name = name;
        this.email = email;
        this.address = address;
        this.number = number;
        this.imgURL = imgURL;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
