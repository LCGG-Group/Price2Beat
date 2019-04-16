package com.lcgg.price2beat;

public class Feeds {
    private String feedsId;
    private String user;
    private String post;


    public Feeds() {}

    public Feeds(String feedsId, String user, String post) {
        this.feedsId = feedsId;
        this.user = user;
        this.post = post;
    }

    public String getFeedsId() {
        return feedsId;
    }
    public String getUser() {
        return user;
    }
    public String getPost() {
        return post;
    }

    public void setFeedsId(String feedsId) {
        this.feedsId = feedsId;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public void setPost(String post) {
        this.post = post;
    }
}
