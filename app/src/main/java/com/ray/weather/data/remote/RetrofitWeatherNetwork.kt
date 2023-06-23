package com.ray.weather.data.remote

import com.ray.weather.data.remote.model.ForecastNetworkResponse
import okhttp3.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitNiaNetwork @Inject constructor(
    okhttpCallFactory: Call.Factory,
) : WeatherNetworkDataSource {

    private val networkApi = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com")
        .callFactory(okhttpCallFactory)
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()
        .create(RetrofitNiaNetworkApi::class.java)


    override suspend fun getCurrentWeather(lat: Long, lng: Long): ForecastNetworkResponse {
        return  networkApi.getCurrentWeather(lat, lng, true).data
    }
}




/**
 * Retrofit API declaration for open-meteo Network API
 */
private interface RetrofitNiaNetworkApi {
    @GET(value = "v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Long,
        @Query("longitude") lng: Long,
        @Query("current_weather") currentWeather: Boolean,
    ): NetworkResponse<ForecastNetworkResponse>

}


private data class NetworkResponse<T>(
    val data: T,
)