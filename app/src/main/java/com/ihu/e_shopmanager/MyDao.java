package com.ihu.e_shopmanager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.orders.Order;
import com.ihu.e_shopmanager.orders.ProductWithQuantity;
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

    @Update
    void updateOrder(Order order);

    @Transaction
    default void updateProductWithRelatedInfo(Product product) {
        // Update the product in the Product table
        updateProduct(product);

        // Update the product in the Order table
        List<Order> orders = getOrders();
        for (Order order : orders) {
            List<ProductWithQuantity> productsWithQuantities = order.getProducts();
            for (ProductWithQuantity productWithQuantity : productsWithQuantities) {
                Product productToUpdate = productWithQuantity.getProduct();
                if (productToUpdate.getId() == product.getId()) {
                    productToUpdate.setName(product.getName());
                    productToUpdate.setCategory(product.getCategory());
                    productToUpdate.setStock(product.getStock());
                    productToUpdate.setPrice(product.getPrice());
                }
            }
            updateOrder(order);
        }

    }
    @Query("select * from Client")
    List<Client> getClients();

    @Query("select * from Product")
    List<Product> getProducts();

    @Query("select * from `order`")
    List<Order> getOrders();

    @Query("SELECT * FROM Product WHERE category = :category")
    List<Product> getProductsInCategory(String category);




}
