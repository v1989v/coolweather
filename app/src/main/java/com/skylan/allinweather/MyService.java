package com.skylan.allinweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.skylan.allinweather.Utils.HttpUtils;
import com.skylan.allinweather.gson.Weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateImage();
        updateWeahter();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int auHour = 8 * 60 * 60 * 1000;
        long trigerAtTime = SystemClock.elapsedRealtime() + auHour;
        Intent intent1 = new Intent(this , MyService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this , 0 ,intent1 ,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP ,trigerAtTime , pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeahter() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weather = sharedPreferences.getString("weather" ,null);
        if (weather != null) {
            String weatherId = new Gson().fromJson(weather , Weather.class).basic.weather_id;
            HttpUtils.sendHttpRequest("http://guolin.tech/api/weather?key=ca4cc032e2334fab8faad46a034425c6&cityid=" +weatherId , new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    try {
                        JSONObject object = new JSONObject(json);
                        String weather = object.getJSONArray("HeWeather").get(0).toString();
                        sharedPreferences.edit().putString("weather" , weather).apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void updateImage() {
        HttpUtils.sendHttpRequest("http://guolin.tech/api/bing_pic" , new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String url = response.body().string();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyService.this);
                sharedPreferences.edit().putString("loadImage" , url).apply();
            }
        });
    }
}
