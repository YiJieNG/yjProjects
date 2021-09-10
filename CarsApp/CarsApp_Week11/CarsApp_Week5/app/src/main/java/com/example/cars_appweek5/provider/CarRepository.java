package com.example.cars_appweek5.provider;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CarRepository {
    private CarDao mCarDao;
    private LiveData<List<Car>> mAllCars;
    private LiveData<List<Car>> mMyCars;

    CarRepository(Application application) {
        CarDatabase db = CarDatabase.getDatabase(application);
        mCarDao = db.carDao();
        mAllCars = mCarDao.getAllCar();
    }

    LiveData<List<Car>> getAllCars() {
        return mAllCars;
    }

    void insert(Car car) {
        CarDatabase.databaseWriteExecutor.execute(() -> mCarDao.addCar(car));
    }

    void deleteAll() {
        CarDatabase.databaseWriteExecutor.execute(() -> {
            mCarDao.deleteAllCars();
        });
    }

    void deleteModel(String Model) {
        CarDatabase.databaseWriteExecutor.execute(() -> {
            mCarDao.deleteModel(Model);
        });
    }

    LiveData<List<Car>> selectModel(String Model){
        return mCarDao.selectModel(Model);
    }
}
