package com.example.rapaid.ui.theme.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rapaid.R
import com.example.rapaid.data.RequestModel
import com.example.rapaid.models.Ambulance
import com.example.rapaid.models.sosRequests
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbulanceDashboard(navController: NavController, context: Context) {
    val requestModel = remember { RequestModel() }

    var requests by remember { mutableStateOf<List<sosRequests>>(emptyList()) }
    var ambulances by remember { mutableStateOf<List<Ambulance>>(emptyList()) }

    // Firestore listeners
    DisposableEffect(Unit) {
        val reqListener = requestModel.listenForSOSRequests { requests = it }
        val ambListener = requestModel.listenForAmbulances { ambulances = it }

        onDispose {
            reqListener.remove()
            ambListener.remove()
        }
    }

    Box {
        // Background image like register screen
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "dashboardBackground",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🚑 Ambulance Dashboard",
                        fontSize = 24.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pending Requests
            item { SectionHeader("📌 Pending Requests") }
            val pendingRequests = requests.filter { it.status == "pending" }
            if (pendingRequests.isEmpty()) {
                item { EmptyCard("No pending requests") }
            } else {
                items(pendingRequests) { req ->
                    PendingRequestCard(req, context, requestModel)
                }
            }

            // Active Requests
            item { SectionHeader("⚡ Active Requests") }
            val activeRequests = requests.filter { it.status == "in_progress" }
            if (activeRequests.isEmpty()) {
                item { EmptyCard("No active requests") }
            } else {
                items(activeRequests) { req ->
                    ActiveRequestCard(
                        request = req,
                        onResolve = { requestModel.markRequestResolved(req.id, context) },
                        context = context
                    )
                }
            }

            // Registered Ambulances
            item { SectionHeader("🚑 Ambulances Registered") }
            if (ambulances.isEmpty()) {
                item { EmptyCard("No ambulances registered") }
            } else {
                items(ambulances) { amb ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        elevation = CardDefaults.cardElevation(8.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Organisation: ${amb.organisation}", fontWeight = FontWeight.Bold, color = Color.Blue)
                            Text("Plate: ${amb.plateNumber}", color = Color.DarkGray)
                            Text("Status: ${amb.status}", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = Color.Blue
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun EmptyCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x80FFFFFF)),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(message, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun PendingRequestCard(req: sosRequests, context: Context, requestModel: RequestModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Request ID: ${req.id}", fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Red)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Location: ${req.latitude}, ${req.longitude}",
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        openMaps(req.latitude, req.longitude, context)
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            val ambulanceId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (ambulanceId.isNotEmpty()) {
                Button(
                    onClick = {
                        requestModel.acceptRequest(req.id, ambulanceId) { success, error ->
                            if (success) {
                                Toast.makeText(context, "✅ Request accepted", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {

                    Spacer(Modifier.width(6.dp))
                    Text("Accept Request", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ActiveRequestCard(
    request: sosRequests,
    onResolve: () -> Unit,
    context: Context
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Request ID: ${request.id}", fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Text("Ambulance: ${request.assignedAmbulanceId ?: "Unassigned"}", color = Color.Gray)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Red)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Location: ${request.latitude}, ${request.longitude}",
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        openMaps(request.latitude, request.longitude, context)
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onResolve,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("Mark Resolved", color = Color.White)
            }
        }
    }
}

// 🔹 Open location in Google Maps
fun openMaps(lat: Double, lng: Double, context: Context) {
    val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(SOS Location)")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")
    context.startActivity(intent)
}
