package com.example.cars_appweek5.provider;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CarDao {

    @Query("select * from car")
    LiveData<List<Car>> getAllCar();

    @Insert
    void addCar(Car car);

    @Query("delete FROM car")
    void deleteAllCars();

    @Query("delete from car where car_model= :model")
    void deleteModel(String model);

    @Query("select * from car where car_model= :model")
    LiveData<List<Car>> selectModel(String model);
}
