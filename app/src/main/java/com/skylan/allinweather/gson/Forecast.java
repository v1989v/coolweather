package com.skylan.allinweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String date;
    public Cond cond;
    public Tmp tmp;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Cond getCond() {
        return cond;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    public Tmp getTmp() {
        return tmp;
    }

    public void setTmp(Tmp tmp) {
        this.tmp = tmp;
    }

    public class Cond{
        @SerializedName("txt_d")
        public String cond_txt;
    }

    public class Tmp{
        public String max;
        public String min;
    }
}
