package com.example.rapaid


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.example.rapaid.data.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

//
//import android.annotation.SuppressLint
//import android.content.Context

//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//
//class LocationHelper(private val context: Context) {
//
//    private val fusedLocationClient: FusedLocationProviderClient =
//        LocationServices.getFusedLocationProviderClient(context)
//
//    @SuppressLint("MissingPermission")
//    fun getCurrentLocation(onLocationReceived: (Double, Double) -> Unit) {
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // No permission
//            return
//        }
//
//        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
//            .addOnSuccessListener { location ->
//                location?.let {
//                    onLocationReceived(it.latitude, it.longitude)
//                }
//            }
//    }
//}



class LocationHelper(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission") // make sure permissions are checked before calling


    fun getCurrentLocation(onResult: (Location?) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            if (loc != null && loc.latitude != 0.0 && loc.longitude != 0.0) {
                // ✅ Valid cached location
                onResult(Location(loc.latitude, loc.longitude))
            } else {
                // ❌ No cached fix → request new location
                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                    .setMaxUpdates(1)
                    .build()

                fusedLocationClient.requestLocationUpdates(
                    request,
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            fusedLocationClient.removeLocationUpdates(this)
                            val freshLoc = result.lastLocation
                            if (freshLoc != null && freshLoc.latitude != 0.0 && freshLoc.longitude != 0.0) {
                                onResult(Location(freshLoc.latitude, freshLoc.longitude))
                            } else {
                                onResult(null) // still invalid
                            }
                        }
                    },
                    Looper.getMainLooper()
                )
            }
        }.addOnFailureListener {
            onResult(null)
        }
    }

}
