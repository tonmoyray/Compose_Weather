package com.ray.weather.ui

import android.Manifest
import android.location.Location
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ray.weather.R
import com.ray.weather.WeatherViewModel
import com.ray.weather.data.remote.model.ForecastNetworkResponse

sealed interface CurrentWeatherUiState {
    object Loading: CurrentWeatherUiState
    data class Success(val forecastNetworkResponse: ForecastNetworkResponse): CurrentWeatherUiState
    data class Error(val code: Int?, val message: String?, val throwable: Throwable?): CurrentWeatherUiState
}

sealed interface CurrentLocationUiState {
    object Loading: CurrentLocationUiState
    object Error: CurrentLocationUiState
    data class Success(val location: Location): CurrentLocationUiState
}


@OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@Composable
fun CurrentLocationScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
){
    val currentLocationState = viewModel.currentLocationUiState.collectAsStateWithLifecycle()

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(key1 = locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            viewModel.getCurrentLocation()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        ) {
        AnimatedContent(
            targetState = locationPermissionsState.allPermissionsGranted
        ) { areGranted ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (areGranted) {
                    when(currentLocationState.value){
                        CurrentLocationUiState.Loading->{
                            Text(text = stringResource(id = R.string.location_loading))
                        }
                        is CurrentLocationUiState.Success->{
                            (currentLocationState.value as CurrentLocationUiState.Success).location.let {
                                Text(text = stringResource(id = R.string.location_success, it.latitude.toString(), it.longitude.toString()))
                            }
                        }
                        else->{
                            Text(text = stringResource(id = R.string.location_error))
                        }
                    }
                } else {
                    Text(text = stringResource(id = R.string.location_permission_rationale))
                    Button(
                        onClick = {
                            locationPermissionsState.launchMultiplePermissionRequest()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.accept))
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
){
    val currentWeatherUiState = viewModel.currentWeatherUiState.collectAsStateWithLifecycle()

    when(currentWeatherUiState.value){
        is CurrentWeatherUiState.Success -> {
            Log.wtf("cray", " success "+ (currentWeatherUiState.value as CurrentWeatherUiState.Success).forecastNetworkResponse)
        }
        is CurrentWeatherUiState.Loading -> {
            Log.wtf("cray"," CurrentWeatherUiState Loading")
        }
        else -> {
            Log.wtf("cray"," CurrentWeatherUiState else")
        }
    }
}

@Preview
@Composable
fun CurrentWeather(){

    Column() {
        Text(text = "Dhaka")
        Text(text = stringResource(id = R.string.temperature, 31))
        Text(text = "Jan, 04, 2023")
        Image(painter = painterResource(id = R.drawable.ic_clear_day), contentDescription = null)
    }
}



/*

0 	Clear sky
1, 2, 3 	Mainly clear, partly cloudy, and overcast
45, 48 	Fog and depositing rime fog
51, 53, 55 	Drizzle: Light, moderate, and dense intensity
56, 57 	Freezing Drizzle: Light and dense intensity
61, 63, 65 	Rain: Slight, moderate and heavy intensity
66, 67 	Freezing Rain: Light and heavy intensity
71, 73, 75 	Snow fall: Slight, moderate, and heavy intensity
77 	Snow grains
80, 81, 82 	Rain showers: Slight, moderate, and violent
85, 86 	Snow showers slight and heavy
95 * 	Thunderstorm: Slight or moderate
96, 99 * 	Thunderstorm with slight and heavy hail

*/



