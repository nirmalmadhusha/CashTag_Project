package com.s23010388.cashtag.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shops")
public class Shop {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "location")
    private String location;

    public Shop(String name, String location){
        this.name=name;
        this.location=location;
    }

    public void setName(String name){
        this.name=name;
    }
    public  void setLocation(String location){
        this.location=location;
    }
    public String getName(){
        return name;
    }
    public String getLocation(){
        return location;
    }

    public int getId() {return id;
    }
}
