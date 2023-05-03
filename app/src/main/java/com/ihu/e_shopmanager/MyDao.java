package com.ihu.e_shopmanager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.products.Product;

import java.util.List;

@Dao
public interface MyDao {

    @Insert
    void insertClient(Client client);
    @Insert
    void insertProduct(Product product);

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

}
