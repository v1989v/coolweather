package com.skylan.allinweather.Utils;

import com.skylan.allinweather.db.City;
import com.skylan.allinweather.db.County;
import com.skylan.allinweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ParseResponse {
    public static void parseProvinceJSON(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Province province = new Province();
                province.setProvinceId(object.getString("id"));
                province.setProvinceName(object.getString("name"));
                province.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseCityJSON(String json , String provinceId) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                City city = new City();
                city.setCityId(object.getString("id"));
                city.setCityName(object.getString("name"));
                city.setProvinceId(provinceId);
                city.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseCountyJSON(String json , String cityId) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                County county = new County();
                county.setCountyId(object.getString("id"));
                county.setCountyName(object.getString("name"));
                county.setWeatherId(object.getString("weather_id"));
                county.setCityId(cityId);
                county.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
