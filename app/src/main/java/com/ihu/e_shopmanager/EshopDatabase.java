package com.ihu.e_shopmanager;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.orders.Order;
import com.ihu.e_shopmanager.products.Product;

@Database(entities = {Client.class, Product.class, Order.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class EshopDatabase extends RoomDatabase {

    public abstract MyDao myDao();
}
