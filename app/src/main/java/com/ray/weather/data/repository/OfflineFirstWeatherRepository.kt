package com.ray.weather.data.repository

import com.ray.weather.data.remote.RetrofitWeatherNetwork
import com.ray.weather.data.remote.model.ForecastNetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OfflineFirstWeatherRepository @Inject constructor(
    private val network: RetrofitWeatherNetwork,
) : WeatherRepository {

    override fun getCurrentWeather(lat: Double, lng: Double): Flow<ForecastNetworkResponse>
        = flow { emit(network.getCurrentWeather(lat, lng)) }

}
