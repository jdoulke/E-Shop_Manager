package com.ihu.e_shopmanager;

import androidx.room.TypeConverter;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.ihu.e_shopmanager.orders.ProductWithQuantity;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromList(List<ProductWithQuantity> items) {
        Gson gson = new Gson();
        return gson.toJson(items);
    }

    @TypeConverter
    public static List<ProductWithQuantity> toList(String itemsString) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<ProductWithQuantity>>() {}.getType();
        return gson.fromJson(itemsString, listType);
    }
}
