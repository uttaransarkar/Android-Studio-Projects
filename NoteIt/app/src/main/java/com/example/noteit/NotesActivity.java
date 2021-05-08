package com.example.noteit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.io.IOException;
import java.util.HashSet;

public class NotesActivity extends AppCompatActivity {

    EditText notesEditText;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        notesEditText = (EditText) findViewById(R.id.notesEditText);

        Intent intent = getIntent();
        id = intent.getIntExtra("NoteID", -1);
        if (id != -1)
            notesEditText.setText(MainActivity.notes.get(id));
        else {
            MainActivity.notes.add("");
            id = MainActivity.notes.size() - 1;
        }


        notesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainActivity.notes.set(id, s.toString());
                MainActivity.arrayAdapter.notifyDataSetChanged();

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.noteit", Context.MODE_PRIVATE);
                try {
//                    sharedPreferences.edit().putString("notes", ObjectSerializer.serialize(MainActivity.notes)).apply();
                    //another way of passing ArrayList via shared preferences is storing it in a HastSet (unordered, but won't matter here)
                    HashSet<String> notesSet = new HashSet<String>(MainActivity.notes);
                    sharedPreferences.edit().putStringSet("notes", notesSet).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}