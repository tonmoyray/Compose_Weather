package com.ray.weather.domain

import com.ray.weather.data.remote.NetworkResult
import com.ray.weather.data.remote.model.ForecastNetworkResponse
import com.ray.weather.data.repository.OfflineFirstWeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val offlineFirstWeatherRepository: OfflineFirstWeatherRepository
) {

    operator fun invoke(lat: Double, lng: Double): Flow<NetworkResult<ForecastNetworkResponse>> {
        return offlineFirstWeatherRepository.getCurrentWeather(lat, lng)
    }
}