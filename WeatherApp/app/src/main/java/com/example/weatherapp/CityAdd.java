package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class CityAdd extends AppCompatActivity {



    public static final String CITYNAME="CITYNAME";
    private EditText ecity;
    Button btnSave;
    static final int PERMISSIONS_REQUEST_INTERNET = 1;
    String link = "https://api.weatherapi.com/v1/current.json?key=3b1755e94452468e80491445231002 &q=";
   // String apikey = "&aqi=no";
    RequestQueue queue;
    CityViewModel cityViewModel;
    Notification notification;
    NotificationManager notificationManager;
    static final String channelID="channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_add);

        ecity=findViewById(R.id.name);
        btnSave=findViewById(R.id.idSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText=ecity.getText().toString();
                if(!searchText.equals("")){
                    createRequest(searchText);
                }
            }
        });
        checkPermissions();
        queue= Volley.newRequestQueue(this);
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
            NotificationChannel notificationChannel=new NotificationChannel(channelID,"Notification for new City",NotificationManager.IMPORTANCE_HIGH);
            notificationManager=getSystemService(NotificationManager.class);


            notificationChannel.setDescription("Notification for current temperature");
            notificationManager.createNotificationChannel(notificationChannel);

        }
        NotificationManagerCompat notikm=NotificationManagerCompat.from(this);
        notikm.notify(m,builder.build());




    }


    public void notification(String title,String text,double temperature){




        //da se ta notifikacija stalno updejtuje tj ona za here da prati lokaciju nonstop

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
//        Intent broadCastIntent=new Intent(this,NotificationReceiver.class);
//        broadCastIntent.putExtra("ime","here");
//        PendingIntent actionIntent=PendingIntent.getBroadcast(this,0,broadCastIntent,PendingIntent.FLAG_UPDATE_CURRENT);


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
            NotificationChannel notificationChannel=new NotificationChannel(channelId,"Notification for new City",NotificationManager.IMPORTANCE_HIGH);
            notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

        }

        notificationManager.notify(m,notification);
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

            String city=ecity.getText().toString();
            double temp_c;
            double  wind_mph;
            double pressure_in;
            double feelslike_c;
            int humidity;
            int cloud;



            JSONObject loc = response.getJSONObject("location");
            String name = loc.getString("name");
            String country = loc.getString("country");
            String localtime= loc.getString("localtime");

            JSONObject curr=response.getJSONObject("current");
            System.out.println(curr + " ++++++ prognoza");
           // System.out.println("============================");

//            int vlaznost= curr.getInt("humidity");
//
//            double pritisak=curr.getDouble("pressure_in");
//            double vetar=curr.getDouble("wind_mph");

//            JSONObject dan1=vlaznost.getInt("humidity");
//            JSONObject dan2=pritisak.getJSONObject("pressure_in");
//            JSONObject dan3=vetar.getJSONObject("wind_mph");



                String temperaturev = "" + curr.get("temp_c");
                if(temperaturev.equalsIgnoreCase("-")){
                    temp_c=0.0;
                }else
                    temp_c = Double.parseDouble(temperaturev);

                System.out.println(curr);

                if(curr==null){
                    temp_c=0.0;
                }else
                    temp_c= (double) curr.getDouble("temp_c");
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
                str.append("Temperature: " + temp_c + celz+ System.lineSeparator());
               // str.append("Current w/mph: " + wind_mph +space+System.lineSeparator());
               // str.append("Current pressure: " + pressure_in +space+ System.lineSeparator());
               // str.append("Current humidity: " + humidity +space+System.lineSeparator());
                str.append("Current cloudiness: " + cloud +space+System.lineSeparator());
                str.append("Feels like: " + feelslike_c +space+celz+System.lineSeparator());



            str1.append("Date and time: " + localtime + System.lineSeparator());
            str1.append("Temperature: " + temp_c + celz+ System.lineSeparator());
                 str1.append("Current w/mph: " + wind_mph +space+System.lineSeparator());
                 str1.append("Current pressure: " + pressure_in +space+ System.lineSeparator());
                 str1.append("Current humidity: " + humidity +space+System.lineSeparator());
            str1.append("Current cloudiness: " + cloud +space+System.lineSeparator());
            str1.append("Feels like: " + feelslike_c +space+celz+System.lineSeparator());



                String asd = str.toString();
                String desc = str1.toString();


                City c;



                c = new City(country, city,desc, (int)temp_c);


                //notification(name, asd, temp_c);
                notification2(name,asd,temp_c);
                cityViewModel=new CityViewModel(this.getApplication());
                cityViewModel.insert(c);


                finish();



        } catch (JSONException e) {

            e.printStackTrace();
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
}