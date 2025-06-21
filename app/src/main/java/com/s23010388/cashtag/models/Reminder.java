package com.s23010388.cashtag.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String date;
    private String time;
    private String description;

    public Reminder(String title, String date, String time, String description){
        this.title=title;
        this.date=date;
        this.time=time;
        this.description=description;
    }
    // Getters and Setters

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }
}
