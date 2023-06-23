package com.ray.weather.ui

import android.Manifest
import android.location.Location
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ray.weather.R
import com.ray.weather.WeatherViewModel
import com.ray.weather.data.remote.model.ForecastNetworkResponse

sealed interface CurrentWeather {
    object Loading: CurrentWeather
    data class Success(val forecastNetworkResponse: ForecastNetworkResponse): CurrentWeather
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
                Modifier.background(Color.Red),
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



