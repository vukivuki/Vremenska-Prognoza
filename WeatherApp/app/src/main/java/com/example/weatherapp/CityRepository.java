package com.example.weatherapp;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CityRepository {

    private CityDao cityDao;
    private LiveData<List<City>> allCities;

    public CityRepository(Application application){
         CityDatabase database = CityDatabase.getInstance(application);
         cityDao = database.cityDao();
         allCities = cityDao.getAllCities();
    }

    public void insert(City city){
        CityDatabase.databaseWriteExecutor.execute(()-> {
            cityDao.insert(city);
        });

    }

    public void update(City city){
        CityDatabase.databaseWriteExecutor.execute(()-> {
            cityDao.update(city);
        });

    }

    public void delete(City city){
        CityDatabase.databaseWriteExecutor.execute(()-> {
            cityDao.delete(city);
        });
    }

    public void deleteAllCities(){
        CityDatabase.databaseWriteExecutor.execute(()-> {
            cityDao.deleteAllCities();
        });

    }

    public LiveData<List<City>> getCities(){
        return allCities;
    }

}
