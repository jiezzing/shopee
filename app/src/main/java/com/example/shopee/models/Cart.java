package com.example.shopee.models;

public class Cart {
    private String id;
    private String name;
    private String description;
    private String price;
    private String qty;
    private String subtotal;
    private String image_uri;
    private String seller_id;

    public Cart() {
    }

    public Cart(String id, String name, String description, String price, String qty, String subtotal, String image_uri, String seller_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.qty = qty;
        this.subtotal = subtotal;
        this.image_uri = image_uri;
        this.seller_id = seller_id;
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

    public String getQty() {
        return qty;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public String getSeller_id() {
        return seller_id;
    }
}
