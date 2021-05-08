package com.example.memorableplaces;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView placeListView;
    static ArrayList<String> places = new ArrayList<String>();
    static ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);

        ArrayList<String> latitudes = new ArrayList<String>();
        ArrayList<String> longitudes = new ArrayList<String>();

        places.clear();
        coordinates.clear();
        latitudes.clear();
        longitudes.clear();

        try {
            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("addresses", ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latds", ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longds", ObjectSerializer.serialize(new ArrayList<String>())));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (places.size()>0 && latitudes.size()>0 && longitudes.size()>0) {
            if (places.size() == latitudes.size() && places.size() == longitudes.size()) {
                for (int i=0; i<latitudes.size(); i++) {
                    coordinates.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
                }
            }
        } else {
            places.add("Add a new place...");
            coordinates.add(new LatLng(0,0));
        }


        placeListView = (ListView) findViewById(R.id.placesListView);
//        places.add("Add a new place...");
//        coordinates.add(new LatLng(0,0));

        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, places);
        placeListView.setAdapter(arrayAdapter);

        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("placeID", position);
                startActivity(intent);
            }
        });

        placeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setTitle("Are you sure?")
                        .setMessage("Delete this place?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                places.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                                try {
                                    sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(MainActivity.places)).apply();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
    }
}