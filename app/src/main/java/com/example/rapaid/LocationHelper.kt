package com.example.rapaid

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.rapaid.data.Location
import com.google.android.gms.location.*

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getValidLocation(onResult: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("LocationHelper", "Permission not granted")
            onResult(null)
            return
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // 10s timeout
        )
            .setMinUpdateIntervalMillis(1000L)
            .setMaxUpdates(5) // try up to 5 times
            .build()

        fusedLocationClient.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc = result.lastLocation
                    if (loc != null && loc.latitude != 0.0 && loc.longitude != 0.0) {
                        fusedLocationClient.removeLocationUpdates(this)
                        onResult(Location(loc.latitude, loc.longitude))
                        Log.d("LocationHelper", "Valid location: $loc")
                    } else {
                        Log.d("LocationHelper", "Invalid location, waiting...")
                    }
                }
            },
            Looper.getMainLooper()
        )
    }
}
