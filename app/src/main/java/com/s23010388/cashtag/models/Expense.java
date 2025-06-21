package com.s23010388.cashtag.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class Expense {
    @PrimaryKey(autoGenerate = true)
    public int id;
    private String title;
    private double amount;
    private String category;
    private long date;

    public Expense(String title,double amount,String category,long date){
        this.title=title;
        this.amount=amount;
        this.category=category;
        this.date=date;
    }

    //Getters
    public String getTitle(){
        return title;
    }
    public double getAmount(){
        return amount;
    }
    public String getCategory(){
        return category;
    }
    public long getDate(){
        return date;
    }

    //Setters
    public void setTitle(String title){
        this.title=title;
    }
    public void setAmount(double amount){
        this.amount=amount;
    }
    public void setCategory(String category){
        this.category=category;
    }
    public void setDate(long date){
        this.date=date;
    }

}
