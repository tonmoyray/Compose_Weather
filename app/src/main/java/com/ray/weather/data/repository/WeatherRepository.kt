package com.ray.weather.data.repository

import com.ray.weather.data.remote.model.ForecastNetworkResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository{

    fun getCurrentWeather(lat: Long, lng: Long): Flow<ForecastNetworkResponse>
}
