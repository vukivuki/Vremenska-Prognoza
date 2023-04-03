package com.example.weatherapp;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(City city);

    @Update
    void update(City city);

    @Delete
    void delete(City city);

    @Query("DELETE FROM City")
    void deleteAllCities();

    @Query("SELECT * FROM City ORDER BY temperature DESC")
    LiveData<List<City>> getAllCities();
}
