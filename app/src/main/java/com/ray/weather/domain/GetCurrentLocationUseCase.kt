package com.ray.weather.domain

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.ray.weather.data.model.LocationUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getCurrentLocation(context: Context): LocationUiModel? {

        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        val isGpsEnabled = locationManager
            .isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGpsEnabled && !(hasAccessCoarseLocationPermission || hasAccessFineLocationPermission)) {
            return  null
        }


        return suspendCancellableCoroutine { cont ->
            fusedLocationProviderClient.lastLocation.apply {
                addOnSuccessListener {
                    var locality = ""
                    var country = ""
                    try {
                        val addresses: MutableList<Address>
                        val geocoder = Geocoder(context, Locale.ENGLISH)
                        addresses = geocoder.getFromLocation(result.latitude, result.longitude, 1) as MutableList<Address>
                        if (addresses.isNotEmpty()) {
                            val fetchedAddress = addresses[0]
                            fetchedAddress.locality?.let {
                                locality = it
                            }
                            fetchedAddress.countryName?.let {
                                country = it
                            }
                        }
                    } catch (e: Exception) {
                        Log.wtf("cray"," getFromLocation "+e.message)
                    }
                    cont.resume(
                        LocationUiModel(
                            result.latitude,
                            result.longitude,
                            locality,
                            country
                        )
                    ) {}  // Resume coroutine with location result
                }
                addOnFailureListener {
                    cont.resume(null ) {} // Resume coroutine with null location result
                }
                addOnCanceledListener {
                    cont.cancel() // Cancel the coroutine
                }
            }
        }
    }
}