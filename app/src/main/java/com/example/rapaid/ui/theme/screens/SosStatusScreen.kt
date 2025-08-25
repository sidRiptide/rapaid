package com.example.rapaid.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rapaid.data.Location
import com.example.rapaid.data.RequestModel

@Composable

fun SosStatusScreen(userId: String, location: Location) {
    val requestModel = remember { RequestModel() }
    var status by remember { mutableStateOf("loading") }
    var isSending by remember { mutableStateOf(false) }

    // 🔹 Listen to SOS status changes from Firestore
    DisposableEffect(Unit) {
        val listener = requestModel.listenToSOSStatus { updatedStatus ->
            status = updatedStatus
        }
        onDispose { listener?.remove() }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🚨 SOS Status", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when (status) {
            "loading" -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Fetching status...")
            }
            else -> {
                val (statusText, statusColor) = when (status.lowercase()) {
                    "pending" -> "Waiting for ambulance..." to Color.Yellow
                    "in_progress", "accepted" -> "🚑 Ambulance on the way!" to Color.Green
                    "resolved" -> "✅ Request resolved" to Color.Blue
                    "rejected" -> "❌ Request rejected" to Color.Red
                    else -> "Unknown status: $status" to MaterialTheme.colorScheme.onBackground
                }

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = statusColor
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                isSending = true
                requestModel.sendSOS(location) { success, error ->
                    isSending = false
                    status = if (success) {
                        "pending"
                    } else {
                        "error: ${error ?: "unknown error"}"
                    }
                }
            },
            enabled = !isSending
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sending...")
            } else {
                Text("Send SOS")
            }
        }
    }
}
