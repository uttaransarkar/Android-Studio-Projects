package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> newsList = new ArrayList<>();
    static ArrayList<String> newsUrlList = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    SQLiteDatabase myDB;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream in = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String inputLine = "";

                while ((inputLine = br.readLine()) != null) {
                    result += inputLine;
                }

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    void retrieveJSONData() {
        Cursor c = myDB.rawQuery("SELECT * FROM news", null);
        int urlIndex = c.getColumnIndex("url");
        c.moveToFirst();
        newsList.clear();
        newsUrlList.clear();
        while (!c.isAfterLast()) {
//            Log.i("URL", c.getString(urlIndex));
            String result = "";
            try {
                DownloadTask task = new DownloadTask();
                result = task.execute(c.getString(urlIndex)).get();
                JSONObject jsonObject = new JSONObject(result);
                String title = jsonObject.getString("title");
                String url = jsonObject.getString("url");
                newsList.add(title);
                newsUrlList.add(url);

            } catch (Exception e) {
                e.printStackTrace();
            }
            c.moveToNext();
        }
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsListView = findViewById(R.id.newsListView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, newsList);
        newsListView.setAdapter(arrayAdapter);

        myDB = this.openOrCreateDatabase("newsDB", MODE_PRIVATE, null);
        myDB.execSQL("CREATE TABLE IF NOT EXISTS news(id INTEGER PRIMARY KEY, url VARCHAR)");
        myDB.execSQL("DELETE FROM news");

        DownloadTask task = new DownloadTask();
        String result = "";


        try {
            result = task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();

            JSONArray jsonArray = new JSONArray(result);
            int numItems = 20;
            if (jsonArray.length() < numItems)
                numItems = jsonArray.length();

            for (int i = 0; i < numItems; i++) {
                String itemID = jsonArray.getString(i);
                String newsurl = "https://hacker-news.firebaseio.com/v0/item/" + itemID + ".json?print=pretty";
                ContentValues values = new ContentValues();
                values.put("url", newsurl);
                myDB.insert("news", null, values);
            }

            retrieveJSONData();

            newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                    intent.putExtra("newsID", position);
                    startActivity(intent);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}