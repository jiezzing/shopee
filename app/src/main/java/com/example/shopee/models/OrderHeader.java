package com.example.shopee.models;

public class OrderHeader {
    private String user_id;
    private String order_no;
    private String total;
    private String status;

    public OrderHeader() {
    }

    public OrderHeader(String user_id, String order_no, String total, String status) {
        this.user_id = user_id;
        this.order_no = order_no;
        this.total = total;
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
