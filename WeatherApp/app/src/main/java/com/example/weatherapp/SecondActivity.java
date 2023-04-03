package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SecondActivity extends AppCompatActivity implements CityAdapter.onCityListener{

    String CityName = "";
    private CityViewModel cityViewModel;
    public String city;
    RequestQueue queue;
    static final int PERMISSIONS_REQUEST_INTERNET = 1;
    String thisCity = "London";
    String link = "https://api.weatherapi.com/v1/current.json?key=3b1755e94452468e80491445231002 &q=";
    int id;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Notification notification;
    NotificationManager notificationManager;
    static final int ID=0;
    static final String channelID="jedan";
    CityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        FloatingActionButton floatingActionButton = findViewById(R.id.idFloating);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, CityAdd.class);
                startActivityForResult(intent, 1);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new CityAdapter(this);
        recyclerView.setAdapter(adapter);





        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);
        cityViewModel.getCities().observe(this, new Observer<List<City>>() {
            @Override
            public void onChanged(List<City> cities) {
                adapter.setCities(cities);
            }
        });



        //prevlacenje desno ce obrisati element
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                cityViewModel.delete(adapter.getCity(viewHolder.getAdapterPosition()));
                Toast.makeText(SecondActivity.this, adapter.getCity(viewHolder.getAdapterPosition()).getCityName() + " is deleted", Toast.LENGTH_SHORT).show();
                CityName = adapter.getCity(viewHolder.getAdapterPosition()).getCityName();
                System.out.println(CityName);

            }
        }).attachToRecyclerView(recyclerView);

        //prevlacenje levo azurira element
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                CityName = adapter.getCity(viewHolder.getAdapterPosition()).getCityName();
                cityViewModel.update(adapter.getCity(viewHolder.getAdapterPosition()));
                id = adapter.getCity(viewHolder.getAdapterPosition()).getId();
                System.out.println(adapter.getCity(viewHolder.getAdapterPosition()).getDesc() + " Description ");
                createRequest(CityName);



            }
        }).attachToRecyclerView(recyclerView);


        //ovo treba da ide pre poziva metoda, ako ne pozvace se prvo metod za null a string cew se proslediti kasnije ali je vec kasno posto program pukne
        checkPermissions();
        queue = Volley.newRequestQueue(this);
    }

    public void notification2(String title,String text,double temperature){

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText("Temperature: "+temperature)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(channelID,"Update Notification",NotificationManager.IMPORTANCE_HIGH);
            notificationManager=getSystemService(NotificationManager.class);


            notificationChannel.setDescription("Temperature Notification");
            notificationManager.createNotificationChannel(notificationChannel);

        }
        NotificationManagerCompat notikm=NotificationManagerCompat.from(this);
        notikm.notify(m,builder.build());


    }

    public void notification(String title,String text,double temperature){

        //da se ta notifikacija stalno updejtuje

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        String channelId=getString(R.string.default_notification_channel_id);
        notification=new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(temperature+"")

                .setColor(Color.BLUE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)
                //.addAction(R.mipmap.ic_launcher,"Refresh",actionIntent)
                .setStyle(new Notification.BigTextStyle()

                        .bigText(text))
                .build();



        notificationManager=
                (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel("ID2","Nesto",NotificationManager.IMPORTANCE_HIGH);
            notificationManager=getSystemService(NotificationManager.class);


            notificationChannel.setDescription("Temperature Notification");
            notificationManager.createNotificationChannel(notificationChannel);

        }

        notificationManager.notify(m,notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meni, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.st1:
                Toast.makeText(this, "Map Opening", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SecondActivity.this, MapsActivity.class);
                startActivity(intent);
                return true;

            case R.id.st3:
                Intent i=new Intent(SecondActivity.this,SettingsActivity.class);
                startActivity(i);

                return true;




            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK){
            String ctName=data.getStringExtra(CityAdd.CITYNAME);

            // Grad grad=new Grad()
        }
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
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
            String ct = CityName;
            double temperature;
            double  wind_mph;
            double pressure_in;
            double feelslike_c;
            int humidity;
            int cloud;


            System.out.println("============================");
            JSONObject loc = response.getJSONObject("location");
            String namea = loc.getString("name");
            String country = loc.getString("country");
            String localtime= loc.getString("localtime");

            JSONObject curr=response.getJSONObject("current");
            System.out.println("============================");
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


            System.out.println("============================");
            String celz="°C";
            String space=" ";
            str.append("Date and time: " + localtime + System.lineSeparator());
            str.append("Temperature: " + temperature + celz+ System.lineSeparator());
            // str.append("Current w/mph: " + wind_mph +space+System.lineSeparator());
            // str.append("Current pressure: " + pressure_in +space+ System.lineSeparator());
            // str.append("Current humidity: " + humidity +space+System.lineSeparator());
            str.append("Current cloudiness: " + cloud +space+System.lineSeparator());
            str.append("Feels like: " + feelslike_c +space+celz+System.lineSeparator());


            String obican = str.toString();

            System.out.println(obican);
            str1.append("Date and time: " + localtime + System.lineSeparator());
            str1.append("Temperature: " + temperature + celz+ System.lineSeparator());
            str1.append("Current cloudiness: " + cloud +space+System.lineSeparator());
            str1.append("Feels like: " + feelslike_c +space+celz+System.lineSeparator());
            str1.append("Current w/mph: " + wind_mph +space+System.lineSeparator());
            str1.append("Current pressure: " + pressure_in +space+ System.lineSeparator());
            str1.append("Current humidity: " + humidity +space+System.lineSeparator());
            str1.append(obican);


            String asd = str.toString();

            City c;


            c = new City(country, namea,obican, (int)temperature);
            c.setId(id);


           // notification(country, asd, temperature);
            notification2(namea,asd,temperature);
            cityViewModel.insert(c);


        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


    @Override
    public void onCityClick(int position) {
        Intent i =new Intent(SecondActivity.this,CityInfoActivity.class);
        i.putExtra("desc",adapter.getCity(position).getDesc());
        startActivity(i);
    }
}