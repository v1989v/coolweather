package com.skylan.allinweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.skylan.allinweather.gson.Weather;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weather = sharedPreferences.getString("weather" , null);
        if(weather != null) {
            Intent intent = new Intent(this , WeatherActivity.class);
            String weatherid=new Gson().fromJson(weather , Weather.class).basic.weather_id;
            intent.putExtra("weather_id" , weatherid);
            startActivity(intent);
            finish();
        }
    }
}
