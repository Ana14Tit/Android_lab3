package com.example.lab3

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ForecastResponse(
    val forecasts: List<WeatherData>
) : Parcelable

@Parcelize
data class WeatherData(
    val location: String,
    val forecast: List<ForecastItem>
) : Parcelable

@Parcelize
data class ForecastItem(
    val date: String,
    val temperature: String,
    val condition: String
) : Parcelable
