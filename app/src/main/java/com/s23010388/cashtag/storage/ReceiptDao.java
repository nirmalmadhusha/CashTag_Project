package com.s23010388.cashtag.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.s23010388.cashtag.models.Receipt;

import java.util.List;

@Dao
public interface ReceiptDao {

    @Insert
    void insert(Receipt receipt);

    @Query("SELECT * FROM receipts WHERE shop_id = :shopId")
    List<Receipt> getReceiptsByShop(int shopId);
}
