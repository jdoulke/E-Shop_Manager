package com.ihu.e_shopmanager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.orders.Order;
import com.ihu.e_shopmanager.products.Product;

import java.util.List;

@Dao
public interface MyDao {

    @Insert
    void insertClient(Client client);
    @Insert
    void insertProduct(Product product);
    @Insert
    void insertOrder(Order order);

    @Delete
    void deleteClient(Client client);
    @Delete
    void deleteProduct(Product product);
    @Update
    void updateClient(Client client);
    @Update
    void updateProduct(Product product);
    @Query("select * from Client")
    List<Client> getClients();

    @Query("select * from Product")
    List<Product> getProducts();

    @Query("select * from `order`")
    List<Order> getOrders();

    @Query("SELECT * FROM Product WHERE category = :category")
    List<Product> getProductsInCategory(String category);




}
