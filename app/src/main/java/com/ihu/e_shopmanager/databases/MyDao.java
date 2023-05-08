package com.ihu.e_shopmanager.databases;

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
    @Delete
    void deleteOrder(Order order);
    @Update
    void updateClient(Client client);
    @Update
    void updateProduct(Product product);
    @Update
    void updateOrder(Order order);
    @Query("select * from Client")
    List<Client> getClients();

    @Query("select * from Product")
    List<Product> getProducts();

    @Query("select * from `order`")
    List<Order> getOrders();

    @Query("SELECT * FROM Product WHERE category = :category")
    List<Product> getProductsInCategory(String category);

    @Query("SELECT * FROM `order` INNER JOIN Client ON `order`.client_id = Client.client_id WHERE `order`.client_id = :clientId")
    List<Order> getClientOrdersFromID(int clientId);

    @Query("SELECT * FROM `order` " +
            "INNER JOIN Client ON `order`.client_id = Client.client_id " +
            "WHERE Client.phone_number = :phoneNumber")
    List<Order> getClientOrdersFromPhoneNumber(long phoneNumber);

    @Query("SELECT * FROM `order` INNER JOIN Client ON `order`.client_id = Client.client_id WHERE  `order`.order_id = :orderID")
    Client getClientFromOrder(int orderID);

    @Transaction
    default void updateProductWithRelatedInfo(Product product) {
        updateProduct(product);

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
            order.setProducts(productsWithQuantities);
            updateOrder(order);
        }
    }



}
