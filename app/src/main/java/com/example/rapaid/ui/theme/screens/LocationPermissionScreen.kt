package com.example.rapaid.ui.theme.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rapaid.navigation.ROUTE_LOCATION_PERMISSION
import com.example.rapaid.navigation.ROUTE_SOS_SCREEN
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LocationPermissionScreen(navController: NavController) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    val activity = context as? Activity

    // Launcher for GPS enable dialog
    val gpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // ✅ GPS turned on → go to SOS screen
            navController.navigate(ROUTE_SOS_SCREEN) {
                popUpTo(ROUTE_LOCATION_PERMISSION) { inclusive = true }
            }
        }
    }

    fun checkLocationSettings() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000L
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // GPS already enabled → go to SOS screen
            navController.navigate(ROUTE_SOS_SCREEN) {
                popUpTo(ROUTE_LOCATION_PERMISSION) { inclusive = true }
            }
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(e.resolution).build()
                    gpsLauncher.launch(intentSenderRequest)
                } catch (_: IntentSender.SendIntentException) {
                }
            }
        }
    }

    when {
        locationPermissionState.status.isGranted -> {
            // ✅ Permission granted → check GPS state
            var showBottomSheet by remember { mutableStateOf(true) }

            if (showBottomSheet) {
                // Bottom sheet UI asking user to turn on GPS
                ModalBottomSheet(
                    onDismissRequest = { /* block dismiss */ }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Enable Location",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "We need your location to send SOS alerts and connect you with the nearest ambulance."
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                showBottomSheet = false
                                checkLocationSettings()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Turn On Location")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = {
                                // Optional: Exit app or go back
                                showBottomSheet = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            } else {
                // UI while waiting
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        else -> {
            // ❌ Permission not granted → show request UI
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚠️ Location permission required for SOS alerts")
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            locationPermissionState.launchPermissionRequest()
                        }
                    ) {
                        Text("Allow Location")
                    }
                }
            }

            // Auto ask first time
            LaunchedEffect(Unit) {
                locationPermissionState.launchPermissionRequest()
            }
        }
    }
}

// Optional fallback helper
private fun isLocationEnabled(context: Context): Boolean {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}
