package com.example.weathertoday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityEditText;
    TextView weatherTextView;

    public class DownloadContent extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream in = conn.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char ch = (char) data;
                    result += ch;
                    data = reader.read();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
//                Log.i("Weather info! ", weatherInfo);

                JSONArray jsonArray = new JSONArray(weatherInfo);
                String res = "";
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    String main = obj.getString("main");
                    String description = obj.getString("description");

                    if (!main.isEmpty() && !description.isEmpty())
                        res += main+ ": " + description + "\n";

                }
                if (res != "")
                    weatherTextView.setText(res);
                else
                    Toast.makeText(getApplicationContext(),"Could not display weather",Toast.LENGTH_SHORT).show();



            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Enter valid city name!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void getWeather(View view) {

        String city = cityEditText.getText().toString();
        //char ch = Character.toUpperCase(city.charAt(0));        //first letter has to be capital

        try {
            String encodedCity = URLEncoder.encode(city, "UTF-8");
            DownloadContent task = new DownloadContent();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=c4cdb6d24bc724dbf0bc5a836b87231c");

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Enter valid city name!",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = (EditText) findViewById(R.id.cityEditText);
        weatherTextView = (TextView) findViewById(R.id.weatherTextView);
    }
}