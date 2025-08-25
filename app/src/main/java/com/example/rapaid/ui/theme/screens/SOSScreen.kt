package com.example.rapaid.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rapaid.data.Location
import com.example.rapaid.data.RequestModel
import com.example.rapaid.navigation.ROUTE_SOS_STATUS
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserSOSScreen(
    navController: NavController,

) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val location = Location(1.2921, 36.8219)
    val context = LocalContext.current
    val requestModel = remember { RequestModel() }

    var isSending by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Idle") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🚨 SOS Status: $status", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                isSending = true
                status = "Sending..."
                requestModel.sendSOS( location ) { success, error ->
                    isSending = false
                    status = if (success) {
                        Toast.makeText(context, "✅ SOS Sent", Toast.LENGTH_SHORT).show()
                        // Optional navigation after success
                        navController.navigate( "$ROUTE_SOS_STATUS/$userId/${location.latitude}/${location.longitude}") {
                            popUpTo("user_sos") { inclusive = true }
                        }
                        "SOS Sent!"
                    } else {
                        Toast.makeText(
                            context,
                            "❌ Failed to send SOS: ${error ?: "Unknown error"}",
                            Toast.LENGTH_LONG
                        ).show()
                        "Failed"
                    }
                }
            },
            enabled = !isSending
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sending...")
            } else {
                Text("Send SOS")
            }
        }
    }
}
