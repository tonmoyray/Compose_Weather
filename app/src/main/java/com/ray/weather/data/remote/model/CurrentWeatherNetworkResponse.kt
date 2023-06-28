package com.ray.weather.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherNetworkResponse(
    @SerialName("temperature") val temperature: Double,
    @SerialName("windspeed") val windSpeed: Double,
    @SerialName("winddirection") val windDirection: Double,
    @SerialName("weathercode") val weatherCode: Int,
    @SerialName("is_day") val isDay: Int,
    @SerialName("time") val time: String
)