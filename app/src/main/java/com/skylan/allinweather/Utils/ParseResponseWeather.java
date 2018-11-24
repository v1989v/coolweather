package com.skylan.allinweather.Utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.skylan.allinweather.gson.Weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class ParseResponseWeather {

    public Weather parseWeatherJSON(String weather_id) {
        String url = "http://guolin.tech/api/weather?key=ca4cc032e2334fab8faad46a034425c6&weather_id="+weather_id;
        HttpUtils.sendHttpRequest(url , new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                if (!TextUtils.isEmpty(json)) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String js =jsonObject.getJSONArray("HeWeather").get(0).toString();
                        Weather weather = new Gson().fromJson(js , Weather.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return null;
    }
}
