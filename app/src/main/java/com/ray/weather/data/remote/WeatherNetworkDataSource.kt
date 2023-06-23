package com.ray.weather.data.remote

import com.ray.weather.data.remote.model.ForecastNetworkResponse

/**
 * Interface representing network calls to the weather API backend
 */
interface WeatherNetworkDataSource {

    suspend fun getCurrentWeather(lat: Long, lng: Long): ForecastNetworkResponse

}
