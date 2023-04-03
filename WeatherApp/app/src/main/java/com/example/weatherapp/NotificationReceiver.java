package com.example.weatherapp;

import android.Manifest;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationReceiver extends BroadcastReceiver {

    String link = "https://api.weatherapi.com/v1/current.json?key=3b1755e94452468e80491445231002 &q=";
    RequestQueue queue;
    FusedLocationProviderClient fusedLocationProviderClient;
    String myCity;
    String city;
    String temperature;


    @Override
    public void onReceive(Context context, Intent intent) {
        queue = Volley.newRequestQueue(context);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);


        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return;
        } else {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            myCity = addresses.get(0).getLocality();
                            System.out.println(myCity);
                            createRequest(myCity, new VolleyCallBack() {


                                @Override
                                public void onSuccess() {
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Nesto")
                                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                                            .setContentTitle("Nesto")
                                            .setContentText("Nesto nesto nesto")
                                            .setOngoing(true)


                                            .setPriority(Notification.PRIORITY_DEFAULT);


                                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

                                    notificationManagerCompat.notify(200, builder.build());


                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        System.out.println("====================================");
    }

    private void createRequest(String searchText, VolleyCallBack callback) {
        String requestUrl = link + searchText;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, requestUrl, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                parseJson(response);
                                callback.onSuccess();

                            }
                        }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void parseJson(JSONObject response) {

        try{
        StringBuilder str1 = new StringBuilder();

        double temp_c;
        double wind_mph;
        double pressure_in;
        double feelslike_c;
        int humidity;
        int cloud;


        JSONObject loc = response.getJSONObject("location");
        String name = loc.getString("name");
        String country = loc.getString("country");
        String localtime = loc.getString("localtime");

        JSONObject curr = response.getJSONObject("current");
        System.out.println(curr + " ++++++ prognoza");


        String temperaturev = "" + curr.get("temp_c");
        if (temperaturev.equalsIgnoreCase("-")) {
            temp_c = 0.0;
        } else
            temp_c = Double.parseDouble(temperaturev);

        System.out.println(curr);

        if (curr == null) {
            temp_c = 0.0;
        } else
            temp_c = (double) curr.getDouble("temp_c");
        if (curr == null) {
            wind_mph = 0.0;
        } else
            wind_mph = (double) curr.getDouble("wind_mph");
        if (curr == null) {
            pressure_in = 0.0;
        } else
            pressure_in = (double) curr.get("pressure_in");
        if (curr == null) {
            humidity = 0;
        } else
            humidity = (int) curr.get("humidity");
        if (curr == null) {
            cloud = 0;
        } else
            cloud = (int) curr.get("cloud");
        if (curr == null) {
            feelslike_c = 0.0;
        } else
            feelslike_c = (double) curr.getDouble("feelslike_c");


        String celz = "°C";
        String space = " ";
        str1.append("Date and time: " + localtime + System.lineSeparator());
        str1.append("Temperature: " + temp_c + celz + System.lineSeparator());
        str1.append("Current cloudiness: " + cloud + space + System.lineSeparator());
        str1.append("Feels like: " + feelslike_c + space + celz + System.lineSeparator());

        String str = str1.toString();
        System.out.println(str);
        city = name;
        temperature =String.valueOf(temp_c);




    }catch (JSONException e) {

            e.printStackTrace();
        }

    }
}


