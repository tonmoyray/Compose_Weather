package com.ray.weather.data.model

data class CurrentWeatherUiModel(
    val city: String,
    val country: String,
    val temperature: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val weatherCode: Int,
    val isDay: Int,
    val time: String
)
