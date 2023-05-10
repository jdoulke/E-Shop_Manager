package com.ihu.e_shopmanager.clients;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "client")
public class Client {

    @PrimaryKey @ColumnInfo (name = "client_id")
    private int id;

    @ColumnInfo (name = "name")
    private String name;

    @ColumnInfo (name = "lastname")
    private String lastname;

    @ColumnInfo (name = "registeration_date")
    private String registeration_date;

    @ColumnInfo (name = "phone_number")
    private long phone_number;

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }


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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRegisteration_date() {
        return registeration_date;
    }

    public void setRegisteration_date(String registeration_date) {
        this.registeration_date = registeration_date;
    }
}
