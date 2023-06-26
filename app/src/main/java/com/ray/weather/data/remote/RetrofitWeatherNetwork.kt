package com.ray.weather.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ray.weather.data.remote.model.ForecastNetworkResponse
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitWeatherNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: Call.Factory,
) : WeatherNetworkDataSource {

    private val networkApi = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com")
        .callFactory(okhttpCallFactory)
        .addConverterFactory(
            networkJson.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(RetrofitNiaNetworkApi::class.java)


    override suspend fun getCurrentWeather(lat: Double, lng: Double): ForecastNetworkResponse {
        return  networkApi.getCurrentWeather(lat, lng, true)
    }
}




/**
 * Retrofit API declaration for open-meteo Network API
 */
private interface RetrofitNiaNetworkApi {
    @GET(value = "v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lng: Double,
        @Query("current_weather") currentWeather: Boolean,
    ): ForecastNetworkResponse

}