package com.lcgg.price2beat;

public class Market {
    private String name;
    private Double price;
    private String store;
    private String imageURL;

    public Market() {}

    public Market(String name, Double price, String store, String imageURL) {
        this.name = name;
        this.price = price;
        this.store = store;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }
    public Double getPrice() {
        return price;
    }
    public String getStore() {
        return store;
    }
    public String getImageURL() {
        return imageURL;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public void setStore(String store) {
        this.store = store;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
