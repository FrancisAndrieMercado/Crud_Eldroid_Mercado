package com.example.crud_firebase_storage_mercado_bsit;

public class FoodModel {
    private String UID;
    private String foodName;
    private String quantity;
    private String price;
    private String description;
    private String imgURL;

    public FoodModel(){}
    public FoodModel(String UID, String foodName, String quantity, String price, String description, String imgURL) {
        this.UID = UID;
        this.foodName = foodName;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
        this.imgURL = imgURL;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
