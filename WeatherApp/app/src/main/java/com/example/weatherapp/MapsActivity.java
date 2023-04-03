package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String link = "https://tile.openweathermap.org/map/clouds_new/1/256/256.png?appid=i2Hkc7MN4.7xwpi";

    private TileOverlay waqiTiles;

    private GoogleMap nMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            //@Nullable
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                int reversedY = (1 << zoom) - y - 1;
                String s = String.format(Locale.ENGLISH, link, zoom, x, y);

                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }

                return url;
            }


        };

        waqiTiles = nMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider));

    }
}
