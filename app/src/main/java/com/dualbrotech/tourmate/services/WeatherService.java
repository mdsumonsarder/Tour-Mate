package com.dualbrotech.tourmate.services;



import com.dualbrotech.tourmate.Models.CurrentWeatherResponse;
import com.dualbrotech.tourmate.Models.ForecastResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Hard-won on 1/16/2018.
 */

public interface WeatherService {
    @GET()
    Call<CurrentWeatherResponse> getCurrentWeatherData(@Url String url);

    @GET()
    Call<ForecastResponse> getForecastWeatherData(@Url String url);
}
