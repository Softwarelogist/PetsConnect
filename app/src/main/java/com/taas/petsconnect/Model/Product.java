package com.taas.petsconnect.Model;

public class Product {
    private String prductname;
    private String productimg;
    private String productdescription;
    private String selectedCategory;
    private int categoryId, quantity;
    private double price, discount, discountprice;
    private String id;
    private String shopId; // Add this field to store the shop ID

    public Product() {
    }

    public Product(String prductname, String productimg, String productdescription, String selectedCategory,
                   int categoryId, int quantity, double price, double discount, double discountprice, String id, String shopId) {
        this.prductname = prductname;
        this.productimg = productimg;
        this.productdescription = productdescription;
        this.selectedCategory = selectedCategory;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.discountprice = discountprice;
        this.id = id;
        this.shopId = shopId;
    }

    public String getPrductname() {
        return prductname;
    }

    public void setPrductname(String prductname) {
        this.prductname = prductname;
    }

    public String getProductimg() {
        return productimg;
    }

    public void setProductimg(String productimg) {
        this.productimg = productimg;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public void setProductdescription(String productdescription) {
        this.productdescription = productdescription;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public double getDiscountprice() {
        return discountprice;
    }

    public void setDiscountprice(double discountprice) {
        this.discountprice = discountprice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}