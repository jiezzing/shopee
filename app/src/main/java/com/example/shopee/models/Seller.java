package com.example.shopee.models;

public class Seller {
    private String seller_no;
    private String order_no;
    private String customer_no;
    private String id;
    private String name;
    private String description;
    private String price;
    private String image_uri;
    private String seller_id;
    private String status;

    public Seller() {
    }

    public Seller(String seller_no, String order_no, String customer_no, String id, String name, String description, String price, String image_uri, String seller_id, String status) {
        this.seller_no = seller_no;
        this.order_no = order_no;
        this.customer_no = customer_no;
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_uri = image_uri;
        this.seller_id = seller_id;
        this.status = status;
    }

    public String getSeller_no() {
        return seller_no;
    }

    public void setSeller_no(String seller_no) {
        this.seller_no = seller_no;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getCustomer_no() {
        return customer_no;
    }

    public void setCustomer_no(String customer_no) {
        this.customer_no = customer_no;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
