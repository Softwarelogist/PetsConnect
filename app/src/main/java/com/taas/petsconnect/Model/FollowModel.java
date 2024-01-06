package com.taas.petsconnect.Model;

public class FollowModel {

    private String followedBy;
    private Long followedAt;
    private  String followingBy;

    public String getFollowingBy() {
        return followingBy;
    }

    public void setFollowingBy(String followingBy) {
        this.followingBy = followingBy;
    }

    public Long getFollowingAT() {
        return followingAT;
    }

    public void setFollowingAT(Long followingAT) {
        this.followingAT = followingAT;
    }

    private Long followingAT;

    public FollowModel() {
    }

    public String getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(String followedBy) {
        this.followedBy = followedBy;
    }


    public Long getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(Long followedAt) {
        this.followedAt = followedAt;
    }
}
