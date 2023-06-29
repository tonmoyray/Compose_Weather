package com.ray.weather.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastNetworkResponse(
    @SerialName("latitude")  val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("current_weather") val currentWeatherNetworkResponse: CurrentWeatherNetworkResponse
)
