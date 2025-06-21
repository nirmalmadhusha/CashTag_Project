package com.s23010388.cashtag.models;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "receipts",
        foreignKeys = @ForeignKey(
        entity = Shop.class,
        parentColumns = "id",
        childColumns = "shop_id",
        onDelete = CASCADE))
public class Receipt {
    @PrimaryKey (autoGenerate = true)
    public int id;

    @ColumnInfo(name = "shop_id")
    public int shopId;

    @ColumnInfo(name = "image_path")
    public String imagePath;

    public Receipt(int shopId, String imagePath){
        this.shopId = shopId;
        this.imagePath = imagePath;
    }
}
