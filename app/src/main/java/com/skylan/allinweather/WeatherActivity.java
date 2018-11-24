package com.skylan.allinweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.skylan.allinweather.Utils.HttpUtils;
import com.skylan.allinweather.gson.Weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String Sweather;
    private Button title_back;
    private TextView title_text;
    private TextView title_now;
    private TextView now_tmp;
    private LinearLayout forecast_layout;
    private TextView now_cond;
    private TextView aqi_text;
    private TextView aqi_pm25;
    private TextView suggestion_comf;
    private TextView suggestion_cw;
    private TextView suggestion_sport;
    private ImageView weatherimage;
    private SwipeRefreshLayout swipeRefreshLayout;
    public DrawerLayout drawerLayout;


    public static void startAttch(Context context , String weatherid) {
        Intent intent = new Intent(context , WeatherActivity.class);
        intent.putExtra("weather_id" , weatherid);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String weatherId = getIntent().getStringExtra("weather_id");
        title_text = findViewById(R.id.title_title);
        title_now  = findViewById(R.id.title_time);
        now_tmp = findViewById(R.id.now_tmp);
        now_cond = findViewById(R.id.now_info);
        forecast_layout = findViewById(R.id.forecast_layout);
        aqi_text = findViewById(R.id.aqi_text);
        aqi_pm25 = findViewById(R.id.aqi_mp25);
        suggestion_comf = findViewById(R.id.suggestion_comf);
        suggestion_cw = findViewById(R.id.suggestion_cw);
        suggestion_sport = findViewById(R.id.suggestion_sport);
        weatherimage = findViewById(R.id.weather_image);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setRefreshing(false);
        loadImage();
        //下拉刷新逻辑
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String json = sharedPreferences.getString("weather" ,null);
                Weather weather = new Gson().fromJson(json , Weather.class);
                refresh(weather.basic.weather_id);
            }
        });
        drawerLayout = findViewById(R.id.drawerlayout);
        title_back = findViewById(R.id.title_back);
        //点击home逻辑
        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        Sweather = sharedPreferences.getString("weather", null);


        if (Sweather != null) {
            Weather weather = new Gson().fromJson(Sweather, Weather.class);
            showWeatherInfo(weather);
        } else {
            refresh(weatherId);
        }
    }

    private void loadImage() {
        String url = "http://guolin.tech/api/bing_pic";
        String s = sharedPreferences.getString("loadImage" , null);
        if (s!=null) {
            Glide.with(this).load(Uri.parse(s)).into(weatherimage);
        } else  {
            HttpUtils.sendHttpRequest(url , new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String s = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(s).into(weatherimage);
                        }
                    });
                }
            });
        }
    }

    public void refresh(String weatherId) {
        loadImage();
        swipeRefreshLayout.setRefreshing(true);
        String url = "http://guolin.tech/api/weather?key=ca4cc032e2334fab8faad46a034425c6&cityid=" + weatherId;
        HttpUtils.sendHttpRequest(url, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject object = new JSONObject(json);
                    String s = object.getJSONArray("HeWeather").get(0).toString();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("weather" , s);
                    editor.apply();
                    final Weather weather = new Gson().fromJson(s, Weather.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            showWeatherInfo(weather);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showWeatherInfo(Weather weather) {
        Intent intent =new Intent(this , MyService.class);
        startService(intent);
        title_text.setText(weather.basic.cityName);
        title_now.setText(weather.basic.update.updateTime.split(" ")[1]);
        now_tmp.setText(weather.now.tmp);
        now_cond.setText(weather.now.cond_txt);
        aqi_text.setText(weather.aqi.city.aqi);
        aqi_pm25.setText(weather.aqi.city.pm25);

        forecast_layout.removeAllViews();
        for (int i = 0 ; i < weather.daily_forecast.size() ; i++) {
            View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item , forecast_layout ,false);
            TextView time = view.findViewById(R.id.date_text);
            time.setText(weather.daily_forecast.get(i).date);
            TextView info = view.findViewById(R.id.info_text);
            info.setText(weather.daily_forecast.get(i).cond.cond_txt);
            TextView max = view.findViewById(R.id.max_text);
            max.setText(weather.daily_forecast.get(i).tmp.max);
            TextView min = view.findViewById(R.id.min_text);
            min.setText(weather.daily_forecast.get(i).tmp.min);
            forecast_layout.addView(view);
        }

        suggestion_comf.setText("舒适度：" +weather.suggestion.comf.txt);
        suggestion_cw.setText("洗车指数：" +weather.suggestion.cw.txt);
        suggestion_sport.setText("运行建议:" +weather.suggestion.sport.txt);
    }
}
