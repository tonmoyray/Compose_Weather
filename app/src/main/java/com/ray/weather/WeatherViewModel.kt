package com.ray.weather

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ray.weather.data.remote.NetworkError
import com.ray.weather.data.remote.NetworkException
import com.ray.weather.data.remote.NetworkSuccess
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
                flowOf(null)  //TODO
            }
        }.map {

            Log.wtf("cray"," map "+it)

            when(it){
                is NetworkSuccess -> CurrentWeatherUiState.Success(it.data)
                is NetworkError -> CurrentWeatherUiState.Error(it.code, it.message, null)
                is NetworkException -> CurrentWeatherUiState.Error(null, null, it.e)
                else -> {null}
            }
        }.stateIn(
            scope = viewModelScope,
            started= SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
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