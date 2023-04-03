package com.example.weatherapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {City.class}, version = 1)
public abstract class CityDatabase extends RoomDatabase {

    private static CityDatabase instance;

    public abstract CityDao cityDao();

    private static final int NUMBER_OF_THREADS=4;
    static final ExecutorService databaseWriteExecutor= Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static  CityDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (CityDatabase.class) {
                if (instance == null) {

                    instance = Room.databaseBuilder(context.getApplicationContext(), CityDatabase.class,
                            "city_base").addCallback(sRoomDatabaseCallback).build();
                }
            }


        }
        return instance;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);

            databaseWriteExecutor.execute(()->{
                CityDao dao=instance.cityDao();
                dao.deleteAllCities();

            });
        }
    };
}
