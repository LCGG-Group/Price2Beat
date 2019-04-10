package com.lcgg.price2beat;

public class Market {
    private String marketId;
    private String name;
    private Double price;
    private String store;
    private String imageURL;
    private Double qty;

    public Market() {}

    public Market(String marketId, String name, Double price, String store, String imageURL, Double qty) {
        this.marketId = marketId;
        this.name = name;
        this.price = price;
        this.store = store;
        this.imageURL = imageURL;
        this.qty = qty;
    }

    public String getMarketId() {
        return marketId;
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
    public Double getQty() {
        return qty;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
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
    public void setQty(Double qty) {
        this.qty = qty;
    }
}
