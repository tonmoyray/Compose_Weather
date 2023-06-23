package com.ray.weather.domain

import com.ray.weather.data.remote.model.ForecastNetworkResponse
import com.ray.weather.data.repository.OfflineFirstWeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val offlineFirstWeatherRepository: OfflineFirstWeatherRepository
) {

    operator fun invoke(lat: Long = 52.52.toLong(), lng: Long = 13.41.toLong() ): Flow<ForecastNetworkResponse> {
        return offlineFirstWeatherRepository.getCurrentWeather(lat, lng)
    }
}