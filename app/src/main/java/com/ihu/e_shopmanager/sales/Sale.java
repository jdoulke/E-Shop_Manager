package com.ihu.e_shopmanager.sales;


import com.ihu.e_shopmanager.products.ProductWithQuantity;


import java.util.List;

public class Sale {

    private float value;
    private int client_id;
    private int sale_id;
    private String sale_date;
    private String order_date;
    private List<ProductWithQuantity> productsList;

    public List<ProductWithQuantity> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<ProductWithQuantity> productsList) {this.productsList = productsList;}

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }
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
