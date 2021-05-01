package com.example.paindairy.weatherApi;

import com.google.gson.annotations.SerializedName;

public class WeatherAPI {

    @SerializedName("main")
    public Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

}
