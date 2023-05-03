package com.ihu.e_shopmanager;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ihu.e_shopmanager.clients.Client;
import com.ihu.e_shopmanager.products.Product;

@Database(entities = {Client.class, Product.class}, version = 2)
public abstract class EshopDatabase extends RoomDatabase {

    public abstract MyDao myDao();
}
