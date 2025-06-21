package com.s23010388.cashtag.storage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.s23010388.cashtag.models.Reminder;

import java.util.List;


@Dao
public interface ReminderDao {
    @Insert
    long insert(Reminder reminder);
    @Delete
    void delete(Reminder reminder);

    @Query("SELECT * FROM reminders ORDER BY id DESC")
    List<Reminder> getAllReminders();


}
