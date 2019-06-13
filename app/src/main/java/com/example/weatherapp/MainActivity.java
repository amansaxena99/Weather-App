package com.example.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityEditText;
    TextView weatherTextView;

    public void checkWeather(View view){
        try{
            downoadTask task = new downoadTask();
            String encodedCityName = URLEncoder.encode(cityEditText.getText().toString(), "UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=b6907d289e10d714a6e88b30761fae22");
            InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);
        } catch (Exception e){
            e.printStackTrace();
            weatherTextView.setText("");
            Toast.makeText(MainActivity.this, "Could not find Weather :(", Toast.LENGTH_SHORT).show();
        }

    }

    public class downoadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream input = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);
                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e){
                e.printStackTrace();
                weatherTextView.setText("");
                Toast.makeText(MainActivity.this, "Could not find Weather :(", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("weather:", weatherInfo);
                JSONArray jsonArray = new JSONArray(weatherInfo);
                String message = "";
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonpart = jsonArray.getJSONObject(i);
                    String main = jsonpart.getString("main");
                    String description = jsonpart.getString("description");
                    Log.i("main:", jsonpart.getString("main"));
                    Log.i("description:", jsonpart.getString("description"));
                    if (!main.equals("") && !description.equals("")){
                        message += main + ":" + description + "\r\n";
                    }
                }
                if (!message.equals("")){
                    weatherTextView.setText(message);
                } else {
                    weatherTextView.setText("");
                    Toast.makeText(MainActivity.this, "Could not find Weather :(", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                weatherTextView.setText("");
                Toast.makeText(MainActivity.this, "Could not find Weather :(", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        weatherTextView = findViewById(R.id.weatherTextView);
    }
}
