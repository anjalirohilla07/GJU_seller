package com.example.zapzoo_seller.models;

public class SingleProduct {
    String id;
    String name;
    String details;
    String category;
    String sub_category;
    String image_url;
    int price;
    String quantity;

    public SingleProduct() {

    }

    public SingleProduct(String id, String name, String details, String category, String sub_category, String image_url, int price, String quantity) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.category = category;
        this.sub_category = sub_category;
        this.image_url = image_url;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
