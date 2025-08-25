package com.example.rapaid.ui.theme.screens


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.rapaid.LocationHelper


@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    var locationText by remember { mutableStateOf("Fetching location...") }

    LaunchedEffect(Unit) {
        locationHelper.getCurrentLocation { lat, lng ->
            locationText = "Lat: $lat, Lng: $lng"
        }
    }

    Text(text = locationText)
}
