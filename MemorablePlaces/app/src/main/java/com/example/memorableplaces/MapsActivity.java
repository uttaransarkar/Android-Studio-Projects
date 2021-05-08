package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    Marker marker;
    LocationManager locationManager;
    LocationListener locationListener;

    public void locateOnMap(Location location, String place) {
        if (location != null) {
//            mMap.clear();
            Log.i("Location", location.getLatitude() + " " + location.getLongitude());
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

            if (marker != null) {
                marker.setPosition(userLocation);
            } else
                marker = mMap.addMarker(new MarkerOptions().position(userLocation).title(place));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locateOnMap(lastKnownLocation, "Here you are!");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }       //generated automatically by bool setMyLocationEnabled()
//        mMap.setMyLocationEnabled(true);

        Intent intent = getIntent();
        int id = intent.getIntExtra("placeID", 0);
        if (id == 0) {
            //zoom in on user location
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locateOnMap(location, "Here you are!");
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }       //generated automatically by bool setMyLocationEnabled()
                    mMap.setMyLocationEnabled(true);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locateOnMap(lastKnownLocation, "Here you are!");
            }
        }

        else {
            Log.i("Hello", "Searching place!");
            LatLng xy = MainActivity.coordinates.get(id);
            Location mem_place = new Location(LocationManager.GPS_PROVIDER);
            mem_place.setLatitude(xy.latitude);
            mem_place.setLongitude(xy.longitude);

            locateOnMap(mem_place, MainActivity.places.get(id));
        }

    }
    @Override
    public void onMapLongClick(LatLng point) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(point.latitude, point.longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                if (addressList.get(0).getThoroughfare() != null) {
                    if (addressList.get(0).getSubThoroughfare() != null)
                        address += addressList.get(0).getSubThoroughfare() + " ";
                    address += addressList.get(0).getThoroughfare() + " ";
                }
                if (addressList.get(0).getLocality() != null)
                    address += addressList.get(0).getLocality();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (address.equals("")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-YYYY_HH:mm");     //sets date format
            address += simpleDateFormat.format(new Date());                                    //Date() gets current date time
        }

        mMap.addMarker(new MarkerOptions().position(point).title(address));
//        Log.i("Address", address);
//        Log.i("Coordinates", point.latitude+" "+point.longitude);
        MainActivity.places.add(address);
        MainActivity.coordinates.add(point);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);

        try {
            ArrayList<String> latitudes = new ArrayList<String>();
            ArrayList<String> longitudes = new ArrayList<String>();

            for (LatLng coord:MainActivity.coordinates) {
                latitudes.add(Double.toString(coord.latitude));
                longitudes.add(Double.toString(coord.longitude));
            }

            sharedPreferences.edit().putString("addresses", ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString("latds", ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("longds", ObjectSerializer.serialize(longitudes)).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(MapsActivity.this, "Location saved!", Toast.LENGTH_SHORT).show();

    }
}