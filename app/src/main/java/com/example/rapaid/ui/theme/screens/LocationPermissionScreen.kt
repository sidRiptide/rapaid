package com.example.rapaid.ui.theme.screens

import com.example.rapaid.navigation.ROUTE_LOCATION_PERMISSION
import com.example.rapaid.navigation.ROUTE_SOS_SCREEN



import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionScreen(navController: NavController) {
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    when {
        locationPermissionState.status.isGranted -> {
            // ✅ Navigate to SOS screen
            LaunchedEffect(Unit) {
                navController.navigate(ROUTE_SOS_SCREEN) {
                    popUpTo(ROUTE_LOCATION_PERMISSION) { inclusive = true }
                }
            }
        }

        else -> {
            // ❌ Permission not granted → show retry UI
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

            // Optional: Auto-ask the first time
            LaunchedEffect(Unit) {
                locationPermissionState.launchPermissionRequest()
            }
        }
    }
}
