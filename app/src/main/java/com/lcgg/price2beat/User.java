package com.lcgg.price2beat;

public class User {
    private String email;
    private String displayName;
    private String firstName;
    private String middleName;
    private String lastName;
    private boolean merchant;
    private String imageURL;


    public User() {
    }

    public User(String email, String displayName, String firstName, String middleName,String lastName, boolean merchant, String imageURL) {
        this.email = email;
        this.displayName = displayName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.merchant = merchant;
        this.imageURL = imageURL;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean getMerchant() {
        return merchant;
    }
    public void setMerchant(boolean merchant) {
        this.merchant = merchant;
    }

    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
