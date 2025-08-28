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
import com.example.rapaid.LocationHelper
import com.example.rapaid.data.Location
import com.example.rapaid.data.RequestModel
import com.example.rapaid.navigation.ROUTE_SOS_STATUS
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserSOSScreen(navController: NavController) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val requestModel = remember { RequestModel() }
    val locationHelper = remember { LocationHelper(context) }

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
                status = "Getting GPS fix..."

                locationHelper.getValidLocation { location ->
                    if (location == null) {
                        isSending = false
                        status = "Failed"
                        Toast.makeText(
                            context,
                            "⚠️ Unable to get valid location. Make sure GPS is ON",
                            Toast.LENGTH_LONG
                        ).show()
                        return@getValidLocation
                    }

                    // ✅ Valid location → send SOS
                    status = "Sending SOS..."
                    requestModel.sendSOS(location) { success, error ->
                        isSending = false
                        status = if (success) {
                            Toast.makeText(context, "✅ SOS Sent", Toast.LENGTH_SHORT).show()
                            navController.navigate(
                                "$ROUTE_SOS_STATUS/$userId/${location.latitude}/${location.longitude}"
                            ) {
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
                }
            },
            enabled = !isSending,
            modifier = Modifier.height(48.dp)
        ) {
            if (isSending) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(status)
                }
            } else {
                Text("Send SOS")
            }
        }
    }
}
