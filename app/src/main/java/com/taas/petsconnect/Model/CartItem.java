package com.taas.petsconnect.Model;

import java.util.UUID;

public class CartItem {
    private String productName;
    private double price;
    private double discount;
    private int quantity;
    private String productimg;
    private String id;
    private String productId;
    private String UserId;
    private String shopId; // Add this field to store the shop ID
    private Product product;

    // Default constructor required for Firebase
    public CartItem() {
    }

    // Constructor with productId parameter
    public CartItem(String productName, double price, double discount, int quantity, String productimg, String UserId, String shopId) {
        this.productName = productName;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
        this.productimg = productimg;
        this.id = generateId();
        this.UserId = UserId;
        this.shopId = shopId;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductimg() {
        return productimg;
    }

    public void setProductimg(String productimg) {
        this.productimg = productimg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    // Sample logic to generate an id (replace with your own logic)
    private String generateId() {
        // Implement your logic to generate an id
        return UUID.randomUUID().toString(); // Using UUID for a unique id
    }
}
