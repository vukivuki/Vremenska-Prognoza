package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviderGetKt;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    CityViewModel cityViewModel;
    FusedLocationProviderClient fusedLocationProviderClient;
    String myCity;
    RequestQueue queue;
    String link = "https://api.weatherapi.com/v1/current.json?key=3b1755e94452468e80491445231002 &q=";
    String Cityname = "";
    static final int PERMISSION_ALL=1;
    static final int ID=0;
    Notification notification;
    NotificationManager notificationManager;
    Notification notification2;


    Button btn;
    Button skip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
//                .getBoolean("isFirstRun", false);
//
//        if (isFirstRun) {
//            //show start activity
//
//            startActivity(new Intent(MainActivity.this, SecondActivity.class));
//            Toast.makeText(MainActivity.this, "First Run", Toast.LENGTH_LONG)
//                    .show();
//        }
//
//
//        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
//                .putBoolean("isFirstRun", true).commit();


        btn = findViewById(R.id.button);
        skip = findViewById(R.id.btnSkip);




        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)) {


                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }


            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(MainActivity.this,SecondActivity.class);
                startActivity(i);
            }
        });

        checkPermissions();
        queue = Volley.newRequestQueue(this);

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
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            myCity = addresses.get(0).getLocality();
                            createRequest(myCity);
                            System.out.println(myCity);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMISSION_ALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int i=0;i<grantResults.length;i++){

            if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                getLocation();
                Intent intent =new Intent(this,SecondActivity.class);
                startActivity(intent);
            }else{
                Intent intent=new Intent(this,SecondActivity.class);
                startActivity(intent);
            }
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

        try {

            StringBuilder str=new StringBuilder();
            StringBuilder str1=new StringBuilder();
            String ct = myCity;
            double temperature;
            double  wind_mph;
            double pressure_in;
            double feelslike_c;
            int humidity;
            int cloud;


            System.out.println("============================");
            JSONObject loc = response.getJSONObject("location");
            String name = loc.getString("name");
            String localtime= loc.getString("localtime");

            JSONObject curr=response.getJSONObject("current");
            System.out.println(curr + " ++++++ prognoza");
            System.out.println("============================");

            int vlaznost= curr.getInt("humidity");

            double pritisak=curr.getDouble("pressure_in");
            double vetar=curr.getDouble("wind_mph");

            int dan1=curr.getInt("humidity");
            double dan2=curr.getDouble("pressure_in");
            double dan3=curr.getDouble("wind_mph");



            String temperaturev = "" + curr.get("temp_c");
            if(temperaturev.equalsIgnoreCase("-")){
                temperature=0.0;
            }else
                temperature = Double.parseDouble(temperaturev);

            System.out.println(curr);

            if(curr==null){
                temperature=0.0;
            }else
                temperature= (double) curr.getDouble("temp_c");
            if(curr==null){
                wind_mph=0.0;
            }else
                wind_mph= (double) curr.getDouble("wind_mph");
            if(curr==null){
                pressure_in=0.0;
            }else
                pressure_in=(double)curr.get("pressure_in");
            if(curr==null){
                humidity=0;
            }else
                humidity=(int)curr.get("humidity");
            if(curr==null){
                cloud=0;
            }else
                cloud=(int)curr.get("cloud");
            if(curr==null){
                feelslike_c=0.0;
            }else
                feelslike_c= (double) curr.getDouble("feelslike_c");

            //System.out.println(temp_c);




            String celz="°C";
            String space=" ";
            str.append("Date and time: " + localtime + System.lineSeparator());
            str.append("Temperature: " + temperature + celz+ System.lineSeparator());
            // str.append("Current w/mph: " + wind_mph +space+System.lineSeparator());
            // str.append("Current pressure: " + pressure_in +space+ System.lineSeparator());
            // str.append("Current humidity: " + humidity +space+System.lineSeparator());
            str.append("Current cloudiness: " + cloud +space+System.lineSeparator());
            str.append("Feels like: " + feelslike_c +space+celz+System.lineSeparator());

            str1.append("Current w/mph: " + wind_mph +space+System.lineSeparator());
            str1.append("Current pressure: " + pressure_in +space+ System.lineSeparator());
            str1.append("Current humidity: " + humidity +space+System.lineSeparator());
            str1.append("++++++++++" + dan1);
            str1.append("++++++++++" + dan2);
            str1.append("++++++++++" + dan3);




            String asd = str.toString();
            String desc = str1.toString();


            City c;


            c = new City(name, ct,desc, (int)temperature);


           // notification(name, asd, temp_c);
          //  notification2(name,asd,temp_c);
            cityViewModel=new CityViewModel(this.getApplication());
            cityViewModel.insert(c);


            finish();


//            }else{
//                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
//            }




        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


}
