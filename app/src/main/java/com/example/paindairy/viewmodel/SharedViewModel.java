package com.example.paindairy.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<Map<String, Double>> weatherApi;

    public SharedViewModel() {
        weatherApi = new MutableLiveData<Map<String, Double>>();
    }

    public void setWeatherApi(double temp, double pressure, double humidity) {
        Map<String, Double> weather = new HashMap<>();

        weather.put("temperature", temp);
        weather.put("pressure", pressure);
        weather.put("humidity", humidity);

        weatherApi.setValue(weather);
    }

    public LiveData<Map<String, Double>> getWeatherApi() {
        return weatherApi;
    }
}
