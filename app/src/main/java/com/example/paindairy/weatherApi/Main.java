package com.example.paindairy.weatherApi;

import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("temp")
    public String temp;

    @SerializedName("pressure")
    public String pressure;

    @SerializedName("humidity")
    public String humidity;

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

}
