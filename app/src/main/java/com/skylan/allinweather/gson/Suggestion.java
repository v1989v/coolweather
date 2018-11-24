package com.skylan.allinweather.gson;

public class Suggestion {
    public Comf comf;
    public Sport sport;
    public Cw cw;

    public Comf getComf() {
        return comf;
    }

    public void setComf(Comf comf) {
        this.comf = comf;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public Cw getCw() {
        return cw;
    }

    public void setCw(Cw cw) {
        this.cw = cw;
    }

    public class Comf {
        public String brf;
        public String txt;
    }

    public class Sport {
        public String brf;
        public String txt;
    }

    public class Cw {
        public String brf;
        public String txt;
    }
}
