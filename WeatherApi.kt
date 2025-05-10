package com.example.lab3

import retrofit2.http.GET
import retrofit2.Response

interface WeatherApi {
    @GET("weather-forecast")
    suspend fun getWeatherData(): Response<ForecastResponse>
}