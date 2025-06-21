package com.s23010388.cashtag.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.s23010388.cashtag.models.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insert(Expense expense);

    @Query("SELECT * FROM Expense")
    List<Expense> getAll();

    @Query("SELECT SUM(amount) FROM Expense")
    Double getTotalSpent();
}
