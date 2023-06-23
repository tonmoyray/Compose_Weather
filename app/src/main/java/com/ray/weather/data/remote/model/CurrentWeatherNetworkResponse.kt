package com.ray.weather.data.remote.model

import com.google.gson.annotations.SerializedName

data class CurrentWeatherNetworkResponse(
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("windspeed") val windSpeed: Double,
    @SerializedName("winddirection") val windDirection: Double,
    @SerializedName("weathercode") val weatherCode: Double,
    @SerializedName("is_day") val isDay: Int,
    @SerializedName("is_day") val time: String
)