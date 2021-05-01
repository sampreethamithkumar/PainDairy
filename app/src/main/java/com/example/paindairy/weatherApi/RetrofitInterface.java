package com.example.paindairy.weatherApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {
    @GET("weather")
    Call<WeatherAPI> weatherApi(@Query("appid") String apiid,
                                @Query("q") String q);
}
