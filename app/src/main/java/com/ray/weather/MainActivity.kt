package com.ray.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ray.weather.ui.CurrentLocationScreen
import com.ray.weather.ui.CurrentWeatherScreen
import com.ray.weather.ui.theme.WeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTheme {
                CurrentLocationScreen()
                CurrentWeatherScreen()
            }
        }
    }
}


