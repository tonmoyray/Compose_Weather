package com.ray.weather

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ray.weather.domain.GetCurrentLocationUseCase
import com.ray.weather.domain.GetCurrentWeatherUseCase
import com.ray.weather.ui.CurrentLocationUiState
import com.ray.weather.ui.CurrentWeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val application: Application,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
): ViewModel(){

    private val _currentLocationUiState: MutableStateFlow<CurrentLocationUiState> = MutableStateFlow(CurrentLocationUiState.Loading)
    val currentLocationUiState= _currentLocationUiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentWeatherUiState: StateFlow<CurrentWeatherUiState?> =
        _currentLocationUiState.flatMapLatest {
            if(it is CurrentLocationUiState.Success){
                getCurrentWeatherUseCase.invoke(it.location.latitude, it.location.longitude)
            }else{
                flowOf(null)
            }
        }.map {
            if(it != null){
                CurrentWeatherUiState.Success(it)
            }else{
                CurrentWeatherUiState.None
            }
        }.stateIn(
            scope = viewModelScope,
            started= SharingStarted.WhileSubscribed(5_000),
            initialValue = CurrentWeatherUiState.None,
        )

    fun getCurrentLocation() {
        viewModelScope.launch {
            getCurrentLocationUseCase.getCurrentLocation(application).let {currentLocation->
                if(currentLocation != null){
                    _currentLocationUiState.value = CurrentLocationUiState.Success(currentLocation)
                }else{
                    _currentLocationUiState.value = CurrentLocationUiState.Error
                }
            }
        }
    }
}