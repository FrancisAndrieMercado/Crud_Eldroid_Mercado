package com.example.crud_firebase_storage_mercado_bsit;

public class ImageModel {
    private String imageName;
    private String imageUrl;


    public ImageModel(String imageName, String imageUrl) {
        if (imageName.trim().equals("")) {
            imageName = "nameless";
        }
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
