package com.ihu.e_shopmanager.orders;



import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.products.Product;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "order",
        foreignKeys = {@ForeignKey(entity = Client.class,
        parentColumns = "client_id",
        childColumns = "client_id",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)})
public class Order {

    @PrimaryKey
    @ColumnInfo(name = "order_id") @NonNull
    private int id;

    @ColumnInfo(name = "client_id") @NonNull
    private int clientId;

    @ColumnInfo(name = "order_date")
    private String orderDate;

    @ColumnInfo(name = "total_price")
    private float totalPrice;

    public List<ProductWithQuantity> getProducts() {
        return products;
    }

    @Ignore
    private Client client;

    public void setProducts(List<ProductWithQuantity> products) {
        this.products = products;
    }

    @ColumnInfo(name = "products")
    private List<ProductWithQuantity> products;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}

