package com.example.shopee.models;

public class OrderDetail {
    private String id;
    private String name;
    private String description;
    private String price;
    private String image_uri;
    private String seller_id;
    private String status;
    private String qty;
    private String subtotal;

    public OrderDetail() {
    }

    public OrderDetail(String id, String name, String description, String price, String image_uri, String seller_id, String status, String qty, String subtotal) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_uri = image_uri;
        this.seller_id = seller_id;
        this.status = status;
        this.qty = qty;
        this.subtotal = subtotal;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public String getStatus() {
        return status;
    }

    public String getQty() {
        return qty;
    }

    public String getSubtotal() {
        return subtotal;
    }
}
