package com.ihu.e_shopmanager.sales;

import com.ihu.e_shopmanager.orders.Order;
import com.ihu.e_shopmanager.products.Product;

import java.util.List;

public class Sale {

    private float value;
    private Order order;
    private int client_id;


    private int sale_id;
    private String sale_date;

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getSale_date() {
        return sale_date;
    }

    public void setSale_date(String sale_date) {
        this.sale_date = sale_date;
    }
}
