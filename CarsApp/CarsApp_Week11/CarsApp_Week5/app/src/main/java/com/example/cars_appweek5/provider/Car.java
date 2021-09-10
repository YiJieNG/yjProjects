package com.example.cars_appweek5.provider;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "car")
public class Car {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "car_id")
    int car_id;

    @ColumnInfo(name = "car_maker")
    String maker;

    @ColumnInfo(name = "car_model")
    String model;

    @ColumnInfo(name = "car_year")
    String year;

    @ColumnInfo(name = "car_color")
    String color;

    @ColumnInfo(name = "car_seats")
    String seats;

    @ColumnInfo(name = "car_price")
    String price;

    public Car(String maker, String model, String year, String color, String seats, String price) {
        this.maker = maker;
        this.model = model;
        this.year = year;
        this.color = color;
        this.seats = seats;
        this.price = price;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(@NonNull int car_id) {
        this.car_id = car_id;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
