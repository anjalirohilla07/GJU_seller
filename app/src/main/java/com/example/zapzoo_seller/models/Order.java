package com.example.zapzoo_seller.models;

import java.sql.Timestamp;
import java.util.List;

public class Order {
    String id;
    String username;
    String status;
    String shop_id;
    List<Product> productList;
    Timestamp timestamp;

    public Order()
    {

    }

    public Order(String id, String username, String status, String shop_id, List<Product> productList, Timestamp timestamp) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.shop_id = shop_id;
        this.productList = productList;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

