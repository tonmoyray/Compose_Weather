package com.ray.weather

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ray.weather.domain.GetCurrentLocationUseCase
import com.ray.weather.ui.CurrentLocationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val application: Application,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
): ViewModel(){

    var currentLocation by mutableStateOf<Location?>(null)

    private val _currentLocationUiState: MutableStateFlow<CurrentLocationUiState> = MutableStateFlow(CurrentLocationUiState.Loading)
    val currentLocationUiState= _currentLocationUiState.asStateFlow()

    fun getCurrentLocation() {
        Log.wtf("cray"," getCurrentLocation ")
        viewModelScope.launch {
            getCurrentLocationUseCase.getCurrentLocation(application).let {currentLocation->
                Log.wtf("cray"," lat ${currentLocation?.latitude} lng ${currentLocation?.longitude}")
                if(currentLocation != null){
                    _currentLocationUiState.value = CurrentLocationUiState.Success(currentLocation)
                }else{
                    _currentLocationUiState.value = CurrentLocationUiState.Error
                }
            }
        }
    }
}