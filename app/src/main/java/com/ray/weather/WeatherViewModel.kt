package com.ray.weather

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ray.weather.data.model.CurrentWeatherUiModel
import com.ray.weather.data.remote.NetworkError
import com.ray.weather.data.remote.NetworkException
import com.ray.weather.data.remote.NetworkSuccess
import com.ray.weather.domain.GetCurrentLocationUseCase
import com.ray.weather.domain.GetCurrentWeatherUseCase
import com.ray.weather.ui.CurrentWeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val application: Application,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
): ViewModel(){

    private val _currentLocationUiState: MutableStateFlow<CurrentWeatherUiState> = MutableStateFlow(CurrentWeatherUiState.LocationLoading)
    val currentLocationUiState= _currentLocationUiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCurrentLocation() {
        viewModelScope.launch {
            getCurrentLocationUseCase.getCurrentLocation(application).let {currentLocation->
                if(currentLocation != null){
                    _currentLocationUiState.value = CurrentWeatherUiState.LocationSuccess(currentLocation)
                    _currentLocationUiState.value = getCurrentWeatherUseCase.invoke(currentLocation.latitude, currentLocation.longitude)
                        .mapLatest { networkResult ->
                            when(networkResult){
                                is NetworkSuccess -> {
                                    CurrentWeatherUiState.WeatherSuccess(
                                        CurrentWeatherUiModel(
                                            currentLocation.city,
                                            currentLocation.country,
                                            networkResult.data.currentWeatherNetworkResponse.temperature,
                                            networkResult.data.currentWeatherNetworkResponse.windDirection,
                                            networkResult.data.currentWeatherNetworkResponse.windSpeed,
                                            networkResult.data.currentWeatherNetworkResponse.weatherCode,
                                            networkResult.data.currentWeatherNetworkResponse.isDay,
                                            networkResult.data.currentWeatherNetworkResponse.time,
                                        )
                                    )
                                }
                                is NetworkError -> CurrentWeatherUiState.WeatherError(networkResult.code, networkResult.message, null)
                                is NetworkException -> CurrentWeatherUiState.WeatherError(null, null, networkResult.e)
                            }
                        }.single()
                }else{
                    _currentLocationUiState.value = CurrentWeatherUiState.LocationError
                }
            }
        }
    }
}