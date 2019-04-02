package com.lcgg.price2beat;

public class Purchases {
    private String item;
    private String payTo;
    private Double amount;
    private String date;
    private String refNumber;
    private boolean claimed;
    private String imageURL;

    public Purchases() {}

    public Purchases(String item, String payTo, Double amount, String date, String refNumber, boolean claimed, String imageURL) {
        this.item = item;
        this.payTo = payTo;
        this.amount = amount;
        this.date = date;
        this.refNumber = refNumber;
        this.claimed = claimed;
        this.imageURL = imageURL;
    }

    public String getItem() {
        return item;
    }
    public String getPayTo() {
        return payTo;
    }
    public Double getAmount() {
        return amount;
    }
    public String getDate() {
        return date;
    }
    public String getRefNumber() {
        return refNumber;
    }
    public boolean getClaimed() {
        return claimed;
    }
    public String getImageURL() {
        return imageURL;
    }

    public void setItem(String item) {
        this.item = item;
    }
    public void setPayTo(String payTo) {
        this.payTo = payTo;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }
    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
