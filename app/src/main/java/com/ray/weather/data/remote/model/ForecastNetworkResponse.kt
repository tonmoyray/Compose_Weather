package com.ray.weather.data.remote.model

import com.google.gson.annotations.SerializedName

data class ForecastNetworkResponse(
    @SerializedName("latitude") val latitude: Long,
    @SerializedName("longitude") val longitude: Long,
    @SerializedName("current_weather") val currentWeatherNetworkResponse: CurrentWeatherNetworkResponse
)
