package com.lcgg.price2beat;

public class Store {
    private String name;
    private String imageURL;

    public Store() {
    }

    public Store(String name, String imageURL) {
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageURL;
    }

    public void setImageUrl(String imageURL) {
        this.imageURL = imageURL;
    }
}
