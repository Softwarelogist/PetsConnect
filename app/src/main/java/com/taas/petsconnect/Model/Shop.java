package com.taas.petsconnect.Model;

public class Shop {
private  String shopId;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    private String shopName;
    private String description;
    private String location;
    private String phoneNumber;
    private  String payment;
    private  String accounttitle;
    private String bankname;
    private String senderacc;





    // Default constructor (required for Firebase)
    public Shop() {
        // Empty constructor
    }

    public Shop(String shopName, String description, String location, String phoneNumber,String payment ,  String accounttitle,String bankname ,String senderacc) {
        this.shopName = shopName;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.payment=payment;
        this.accounttitle=accounttitle;
        this.senderacc=senderacc;
        this.bankname=bankname;

    }

    // Getters and Setters
    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getSenderacc() {
        return senderacc;
    }

    public void setSenderacc(String senderacc) {
        this.senderacc = senderacc;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getAccounttitle() {
        return accounttitle;
    }

    public void setAccounttitle(String accounttitle) {
        this.accounttitle = accounttitle;
    }

    public String getBankname() {
        return bankname;
    }


}

