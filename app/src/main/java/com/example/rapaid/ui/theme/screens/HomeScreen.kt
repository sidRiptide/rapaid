package com.example.rapaid.ui.theme.screens


import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.rapaid.LocationHelper
import com.example.rapaid.navigation.ROUTE_HOME
import com.example.rapaid.navigation.ROUTE_SOS_SCREEN
//
//@Composable
//fun HomeScreen(navController: NavController) {
//    val context = LocalContext.current
//    var hasPermission by remember { mutableStateOf(false) }
//
//    if (!hasPermission) {
//        // ✅ Ask for permission
//        LocationPermissionRequest {
//            hasPermission = true // update when granted
//        }
//    } else {
//        // ✅ Safe to use location now
//        val locationHelper = remember { LocationHelper(context) }
//        var locationText by remember { mutableStateOf("Fetching location...") }
//
//        LaunchedEffect(Unit) {
//            locationHelper.getCurrentLocation { location ->
//                location?.let {
//                    locationText = "Lat: ${it.latitude}, Lng: ${it.longitude}"
//
//                    // 🚀 Once we have location, go to SOS screen
//                    navController.navigate(ROUTE_SOS_SCREEN) {
//                        popUpTo(ROUTE_HOME) { inclusive = true }
//                    }
//                } ?: run {
//                    locationText = "Unable to get location"
//                }
//            }
//        }
//
//        Text(locationText)
//    }
//}
//
//@Composable
//fun LocationPermissionRequest(
//    onGranted: () -> Unit
//) {
//    val context = LocalContext.current
//    var permissionRequested by remember { mutableStateOf(false) }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            onGranted()
//        } else {
//            Toast.makeText(context, "❌ Location permission denied", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        if (!permissionRequested) {
//            permissionRequested = true
//            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }
//}
//
//
