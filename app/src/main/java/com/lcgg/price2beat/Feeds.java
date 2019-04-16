package com.lcgg.price2beat;

public class Feeds {
    private String feedsId;
    private String post;
    private Double like;
    private Double share;


    public Feeds() {}

    public Feeds(String feedsId, String post, Double like, Double share) {
        this.feedsId = feedsId;
        this.post = post;
        this.like = like;
        this.share = share;
    }

    public String getFeedsId() {
        return feedsId;
    }
    public String getPost() {
        return post;
    }
    public Double getLike() {
        return like;
    }
    public Double getShare() {
        return share;
    }

    public void setFeedsId(String feedsId) {
        this.feedsId = feedsId;
    }
    public void setPost(String post) {
        this.post = post;
    }
    public void setLike(Double like) {
        this.like = like;
    }
    public void setShare(Double share) {
        this.share = share;
    }
}
