package com.ihu.e_shopmanager.products;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "product")
public class Product {

    @PrimaryKey
    @ColumnInfo(name = "product_id") @NonNull
    private int id;

    @ColumnInfo (name = "name")
    private String name;

    @ColumnInfo (name = "category")
    private String category;

    @ColumnInfo (name = "stock")
    private int stock;

    @ColumnInfo (name = "price")
    private float price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }


}
