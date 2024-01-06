package com.taas.petsconnect.Model;

public class ShopVerificationRequest {

    private String userId;
    private String shopName;
    private String status;
    private String payment;
    private  String accounttitle;
    private String bankname;
    private String senderacc;

    private  String  receiptimage;


    // Default constructor (required for Firebase)
    public ShopVerificationRequest() {
    }

    // Updated constructor to include userId
    public ShopVerificationRequest(String userId, String shopName, String status, String payment,String  receiptimage,  String accounttitle,String bankname ,String senderacc) {
        this.userId = userId;
        this.shopName = shopName;
        this.status = status;
        this.payment = payment;
        this.receiptimage=receiptimage;
        this.accounttitle = accounttitle;
        this.bankname = bankname;
        this.senderacc = senderacc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
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

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getSenderacc() {
        return senderacc;
    }

    public void setSenderacc(String senderacc) {
        this.senderacc = senderacc;
    }

    public String getReceiptimage() {
        return receiptimage;
    }

    public void setReceiptimage(String receiptimage) {
        this.receiptimage = receiptimage;
    }

}
