package com.skylan.allinweather;

import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.gson.Gson;
import com.skylan.allinweather.Utils.HttpUtils;
import com.skylan.allinweather.gson.Weather;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "ExampleInstrumentedTest";
    @Test
    public void useAppContext() {
        String uri = "http://guolin.tech/api/weather?cityid=CN101110805&key=ca4cc032e2334fab8faad46a034425c6";
        HttpUtils.sendHttpRequest(uri , new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String sjon = jsonObject.getJSONArray("HeWeather").get(0).toString();
                    Weather weather = new Gson().fromJson(sjon , Weather.class);
                    Log.d(TAG, "onResponse: " +weather.status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
