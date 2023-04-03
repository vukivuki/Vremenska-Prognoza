package com.example.weatherapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CityViewModel extends AndroidViewModel {

    private CityRepository repository;
    private LiveData<List<City>> cities;

    public CityViewModel(@NonNull Application application) {
        super(application);
        repository = new CityRepository(application);
        cities = repository.getCities();
    }

    public void insert(City city){
        repository.insert(city);
    }

    public void update(City city){
        repository.update(city);
    }

    public void delete(City city){
        repository.delete(city);
    }

    public void deleteAllGrads(){
        repository.deleteAllCities();
    }

    public LiveData<List<City>> getCities(){
        return cities;
    }
}
