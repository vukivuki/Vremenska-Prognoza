package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    TextView tvDaily;
    Switch sw;
    RequestQueue queue;
    static final int PERMISSIONS_REQUEST_INTERNET = 1;
    String link = "https://api.weatherapi.com/v1/current.json?key=3b1755e94452468e80491445231002 &q=";
    private FusedLocationProviderClient fusedLocationProviderClient;
    String myCity;
    String temperature;
    String cityI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvDaily=findViewById(R.id.tvDaily);
        sw=findViewById(R.id.idSwitch);
        createNotificationChannel();

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(SettingsActivity.this);

        SharedPreferences sharedPreferences=getSharedPreferences("save",MODE_PRIVATE);
        sw.setChecked(sharedPreferences.getBoolean("value",false));


        if (ActivityCompat.checkSelfPermission(SettingsActivity.this
                , android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && (ActivityCompat.checkSelfPermission(SettingsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)) {






        } else {
            ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sw.isChecked()){


                    System.out.println("Turn on");
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",true);
                    editor.apply();
                    sw.setChecked(true);


                    Intent activityIntent=new Intent(SettingsActivity.this,NotificationReceiver.class);
                    activityIntent.putExtra("temp_c",temperature);
                    activityIntent.putExtra("name",cityI);

                    PendingIntent pendingIntent=PendingIntent.getBroadcast(SettingsActivity.this,0,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);


                    System.out.println("-----------------------------");
                    long time=System.currentTimeMillis();
                    long timet=1000*10; //1000*10*6*60*8

                    Calendar calendar = Calendar.getInstance();





                    calendar.set(Calendar.MINUTE, 00);
                    calendar.set(Calendar.SECOND, 00);
                    calendar.set(Calendar.MILLISECOND,00);



                 //    alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                   // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, time + timet, pendingIntent);



                }else{
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    sw.setChecked(false);

                }
            }
        });

        checkPermissions();
        queue= Volley.newRequestQueue(this);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name="This";
            String desc="Description";
            int importance=NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel("Nesto",name,importance);
            channel.setDescription(desc);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void getLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {



            return;
        }else {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(SettingsActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            myCity = addresses.get(0).getLocality();
                            System.out.println(myCity);
                            createRequest(myCity);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }

    }

    private void createRequest(String searchText) {
        String requestUrl = link + searchText;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, requestUrl, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                parseJson(response);
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


            System.out.println("-----------------------------");
            String celz = "°C";
            String space = " ";
            str1.append("Date and time: " + localtime + System.lineSeparator());
            str1.append("Temperature: " + temp_c + celz + System.lineSeparator());
            str1.append("Current cloudiness: " + cloud + space + System.lineSeparator());
            str1.append("Feels like: " + feelslike_c + space + celz + System.lineSeparator());

            String str = str1.toString();
            System.out.println(str);
            cityI = name;
            temperature =String.valueOf(temp_c);




        }catch (JSONException e) {

            e.printStackTrace();
        }

    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMISSIONS_REQUEST_INTERNET);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_INTERNET: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}