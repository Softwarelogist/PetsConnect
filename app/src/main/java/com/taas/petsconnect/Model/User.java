package com.taas.petsconnect.Model;

import java.util.Map;


public class User {
    public User() {
    }
    private String uname;
    private String email;
    private String password;
    private String cover_Pic;
    private String uprofile;
    private String uprofession;
    private String phone;
    private String UserID;

    public User(String uname, String email, String password, String cover_Pic, String uprofile, String uprofession, String phone, String userID) {
        this.uname = uname;
        this.email = email;
        this.password = password;
        this.cover_Pic = cover_Pic;
        this.uprofile = uprofile;
        this.uprofession = uprofession;
        this.phone = phone;
        UserID = userID;
    }

    public User(String uname, String email, String password, String uprofession, String phone) {
        this.uname = uname;
        this.email = email;
        this.password = password;
        this.cover_Pic = cover_Pic;
        this.uprofile = uprofile;
        this.uprofession = uprofession;
        this.phone = phone;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCover_Pic() {
        return cover_Pic;
    }

    public void setCover_Pic(String cover_Pic) {
        this.cover_Pic = cover_Pic;
    }

    public String getUprofile() {
        return uprofile;
    }

    public void setUprofile(String uprofile) {
        this.uprofile = uprofile;
    }

    public String getUprofession() {
        return uprofession;
    }

    public void setUprofession(String uprofession) {
        this.uprofession = uprofession;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }




    private int followerCount;
    private int followingCount;
    private int postCount;
    private boolean isFollowed;
    private boolean followed;

    private Map<String, Boolean> followedBy; // Map of UIDs who follow this user

    public Map<String, Boolean> getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(Map<String, Boolean> followedBy) {
        this.followedBy = followedBy;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }





}