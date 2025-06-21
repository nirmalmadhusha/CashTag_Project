package com.s23010388.cashtag.storage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.s23010388.cashtag.models.Expense;
import com.s23010388.cashtag.models.Reminder;
import com.s23010388.cashtag.models.Shop;
import com.s23010388.cashtag.models.Receipt;

@Database(entities = {Expense.class , Shop.class , Receipt.class , Reminder.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract ExpenseDao expenseDao();
    public abstract ShopDao shopDao();
    public abstract ReceiptDao receiptDao();
    public abstract ReminderDao reminderDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "cashtag_db"
            ).fallbackToDestructiveMigration(true).allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}
