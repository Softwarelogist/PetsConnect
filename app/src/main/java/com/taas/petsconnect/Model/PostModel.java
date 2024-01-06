package com.taas.petsconnect.Model;
public class PostModel {
    private String postId;
    private String postimage;
    private String postvideo;
    private String postedBy;
    private long postedAt;
    private String postdes;
    private int postLike;
    private int commentCount;

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public PostModel(int commentCount) {
        this.commentCount = commentCount;
    }

    public PostModel(String postId, String postimage, String postvideo, String postedBy, long postedAt, String postdes) {
        this.postId = postId;
        this.postimage = postimage;
        this.postvideo = postvideo;
        this.postedBy = postedBy;
        this.postedAt = postedAt;
        this.postdes = postdes;

    }

    public PostModel() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getPostvideo() {
        return postvideo;
    }

    public void setPostvideo(String postvideo) {
        this.postvideo = postvideo;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public long getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(long postedAt) {
        this.postedAt = postedAt;
    }

    public String getPostdes() {
        return postdes;
    }

    public void setPostdes(String postdes) {
        this.postdes = postdes;
    }
    public int getPostLike() {
        return postLike;
    }

    public void setPostLike(int postLike) {
        this.postLike = postLike;
    }
}