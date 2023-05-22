package com.ihu.e_shopmanager.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.orders.Order;
import com.ihu.e_shopmanager.products.ProductWithQuantity;
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

    @Query("Select * From CLIENT where client_id = :client_id")
    Client getClientFromId(int client_id);
    @Query("Select * From CLIENT where phone_number = :phone_number")
    Client getClientFromPhone(long phone_number);

    @Query("Select * From product where product_id = :product_id")
    Product getProductFromId(int product_id);

    @Query("Select * From `order` where order_id = :order_id")
    Order getOrderFromId(int order_id);
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

    @Query("Select * from `order` where `order`.order_date = '08/05/2023'")
    List<Order> getQuery3();

    @Query("Select * from `order` where `order`.total_price > 2000")
    List<Order> getQuery4();

    @Query("Select * from product where product.stock > 20")
    List<Product> getQuery5();

    @Query("Select * from product where product.category = 'Laptop'")
    List<Product> getQuery6();

    @Query("SELECT * FROM `client` WHERE client_id IN (SELECT DISTINCT client_id FROM `order` WHERE `order`.total_price > 2500)")
    List<Client> getQuery7();

    @Query("SELECT * FROM `order` WHERE client_id NOT IN (SELECT client_id FROM client WHERE name = 'Κώστας')")
    List<Order> getQuery8();


}
