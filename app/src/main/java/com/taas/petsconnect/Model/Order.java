package com.taas.petsconnect.Model;

import java.util.List;

public class Order {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    private String name;
    private String email;
    private String phone;
    private String address;
    private String date;
    private String comments;
    private List<CartItem> cartItems;
    private double subtotal;
    private double tax;
    private double total;
    private String productId;
    private Product product;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    private String shopId;

    // Default constructor required for Firebase
    public Order() {
    }

    public Order(String name, String email, String phone, String address, String date, String comments,
                 List<CartItem> cartItems, double subtotal, double tax, double total,String shopId/*, String productId, Product product*/) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.date = date;
        this.comments = comments;
        this.cartItems = cartItems;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.shopId=shopId;
       /* this.productId = productId;
        this.product = product;*/
    }

    // Add getters and setters as needed

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


}
