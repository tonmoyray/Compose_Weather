package com.ray.weather.ui

import android.Manifest
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ray.weather.R
import com.ray.weather.WeatherViewModel
import com.ray.weather.data.remote.model.ForecastNetworkResponse
import com.ray.weather.ui.theme.Purple80

sealed interface CurrentWeatherUiState {
    object LocationLoading: CurrentWeatherUiState
    object LocationError: CurrentWeatherUiState
    object WeatherLoading: CurrentWeatherUiState
    data class WeatherSuccess(val forecastNetworkResponse: ForecastNetworkResponse): CurrentWeatherUiState
    data class WeatherError(val code: Int?, val message: String?, val throwable: Throwable?): CurrentWeatherUiState
}


@OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@Composable
fun CurrentLocationScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
){
    val currentWeatherUiState = viewModel.currentLocationUiState.collectAsStateWithLifecycle()

    val fontFamily = FontFamily(
        Font(R.font.lexend_black, FontWeight.Light),
        Font(R.font.lexend_bold, FontWeight.Normal),
        Font(R.font.lexend_extra_bold, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.lexend_extra_light, FontWeight.Medium),
        Font(R.font.lexend_light, FontWeight.Bold),
        Font(R.font.lexend_medium, FontWeight.Bold),
        Font(R.font.lexend_regular, FontWeight.Bold),
        Font(R.font.lexend_semi_bold, FontWeight.Bold),
        Font(R.font.lexend_thin, FontWeight.Bold)
    )

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
        modifier = Modifier.fillMaxSize().background(color = Purple80),
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
                    when(currentWeatherUiState.value){
                        CurrentWeatherUiState.LocationLoading->{
                            Log.wtf("cray", " LocationLoading ");
                            Text(text = stringResource(id = R.string.location_loading))
                        }
                        is CurrentWeatherUiState.LocationError->{
                            Log.wtf("cray", " LocationError ");
                            Text(text = stringResource(id = R.string.location_error))
                        }
                        is CurrentWeatherUiState.WeatherSuccess -> {
                            Log.wtf("cray", " success "+ (currentWeatherUiState.value as CurrentWeatherUiState.WeatherSuccess).forecastNetworkResponse)
                            val forecastNetworkResponse = (currentWeatherUiState.value as CurrentWeatherUiState.WeatherSuccess).forecastNetworkResponse
                            val temperature = forecastNetworkResponse.currentWeatherNetworkResponse.temperature
                            val weatherCode = forecastNetworkResponse.currentWeatherNetworkResponse.weatherCode
                            val isDay = forecastNetworkResponse.currentWeatherNetworkResponse.isDay

                            val weatherIcon =
                                when(weatherCode){
                                    1-> {
                                        if(isDay == 1){
                                            R.drawable.ic_clear_day
                                        }else{
                                            R.drawable.ic_clear_night
                                        }
                                    }
                                    2-> {
                                        if(isDay == 1){
                                            R.drawable.ic_partly_cloudy_day
                                        }else{
                                            R.drawable.ic_partly_cloudy_night
                                        }
                                    }
                                    3-> R.drawable.ic_overcast
                                    45, 48 -> R.drawable.ic_fog
                                    51, 53, 55 -> R.drawable.ic_showers
                                    56, 57 -> R.drawable.ic_sleet
                                    61, 63, 65, 80, 81, 82 -> R.drawable.ic_heavy_showers
                                    66, 67, 85, 86  -> R.drawable.ic_heavy_sleet
                                    71, 73, 75 -> R.drawable.ic_snow
                                    77 -> R.drawable.ic_heavy_snow
                                    95 -> R.drawable.ic_thunderstorm_showers
                                    96, 99  -> R.drawable.ic_thunderstorm_snow
                                    else -> R.drawable.ic_cloudy
                                }

                            Column() {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Dhaka",
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontFamily = fontFamily,
                                    style = MaterialTheme.typography.headlineLarge,
                                )
                                Image(
                                    modifier = Modifier.fillMaxWidth(),
                                    painter = painterResource(id = weatherIcon),
                                    contentDescription = null
                                )
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(id = R.string.temperature, temperature.toString()),
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontFamily = fontFamily,
                                    style = MaterialTheme.typography.displayLarge,
                                )
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Jan, 04, 2023",
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = fontFamily
                                )
                            }
                        }
                        is CurrentWeatherUiState.WeatherLoading -> {
                            Log.wtf("cray"," CurrentWeatherUiState Loading")
                            Text(text = stringResource(id = R.string.weather_loading))
                        }
                        is CurrentWeatherUiState.WeatherError -> {
                            Log.wtf("cray"," CurrentWeatherUiState Loading")
                            Text(text = stringResource(id = R.string.weather_error))
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







