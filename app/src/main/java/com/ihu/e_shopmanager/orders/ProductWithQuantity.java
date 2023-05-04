package com.ihu.e_shopmanager.orders;

import com.ihu.e_shopmanager.products.Product;

public class ProductWithQuantity {

    private Product product;

    private int quantity;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
