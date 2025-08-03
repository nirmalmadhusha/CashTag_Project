package com.s23010388.cashtag.storage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.s23010388.cashtag.models.Shop;

import java.util.List;

@Dao
public interface ShopDao {
    @Insert
    void insert(Shop shop);

    @Query("SELECT * FROM shops")
    List<Shop> getAllShops();

    @Delete
    void delete(Shop shop);

}