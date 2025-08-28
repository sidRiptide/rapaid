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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.google.firebase.firestore.FirebaseFirestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbulanceDashboard(navController: NavController, context: Context) {
    val requestModel = remember { RequestModel() }

    var requests by remember { mutableStateOf<List<sosRequests>>(emptyList()) }
    var ambulances by remember { mutableStateOf<List<Ambulance>>(emptyList()) }
    var myAmbulance by remember { mutableStateOf<Ambulance?>(null) }
    var showAmbulanceDialog by remember { mutableStateOf(false) }

    // Firestore listeners
    DisposableEffect(Unit) {
        val reqListener = requestModel.listenForSOSRequests { rawRequests ->
            requests = rawRequests.filter { it.latitude != 0.0 && it.longitude != 0.0 }
        }

        val ambListener = requestModel.fetchAllAmbulances { list ->
            ambulances = list
            val currentEmail = FirebaseAuth.getInstance().currentUser?.email
            myAmbulance = list.find { amb -> amb.driver == currentEmail }
        }

        onDispose {
            reqListener.remove()
            ambListener.remove()
        }
    }

    // Background
    Box {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "dashboardBackground",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ambulance Dashboard",
                        fontSize = 22.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("All Registered Ambulances") },
                            onClick = {
                                expanded = false
                                showAmbulanceDialog = true
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D47A1))
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
            item { SectionHeader("Pending Requests") }
            val pendingRequests = requests.filter { it.status == "pending" }
            if (pendingRequests.isEmpty()) {
                item { EmptyCard("No pending requests") }
            } else {
                items(pendingRequests) { req ->
                    PendingRequestCard(req, context, requestModel, myAmbulance)
                }
            }

            // Active Requests
            item { SectionHeader("Active Requests") }
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

            // My Ambulance Info
            item { SectionHeader("My Ambulance") }

            if (myAmbulance != null) {
                item { MyAmbulanceCard(myAmbulance!!) }
            } else {
                item { EmptyCard("No ambulance linked to your account") }
            }
        }
    }

    // Dialog showing all registered ambulances
    if (showAmbulanceDialog) {
        AlertDialog(
            onDismissRequest = { showAmbulanceDialog = false },
            confirmButton = {
                TextButton(onClick = { showAmbulanceDialog = false }) {
                    Text("Close")
                }
            },
            title = { Text("All Registered Ambulances") },
            text = {
                LazyColumn {
                    items(ambulances) { amb ->
                        Column(Modifier.padding(vertical = 6.dp)) {
                            Text("ID: ${amb.id}", fontWeight = FontWeight.Bold)
                            Text("Emailb b6yhmj: ${amb.driver}")
                            Text("Plate: ${amb.plateNumber}")
                            Text("Organisation: ${amb.organisation}")
                            Divider(Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        )
    }
}

// 🔹 My Ambulance Card (editable & deletable)
@Composable
fun MyAmbulanceCard(myAmbulance: Ambulance) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2B2B).copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Email: ${myAmbulance.driver}", fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))
            Text("Organisation: ${myAmbulance.organisation}", color = Color.White)
            Text("Plate: ${myAmbulance.plateNumber}", color = Color.White)
            Text("Status: ${myAmbulance.status}", color = Color.White.copy(alpha = 0.9f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { showEditDialog = true }) { Text("Edit") }
                Button(onClick = { showDeleteDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Delete") }
            }
        }
    }

    // Edit Dialog
    if (showEditDialog) {
        var newOrg by remember { mutableStateOf(myAmbulance.organisation) }
        var newPlate by remember { mutableStateOf(myAmbulance.plateNumber) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Ambulance") },
            text = {
                Column {
                    OutlinedTextField(value = newOrg, onValueChange = { newOrg = it }, label = { Text("Organisation") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newPlate, onValueChange = { newPlate = it }, label = { Text("Plate Number") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    FirebaseFirestore.getInstance()
                        .collection("ambulances")
                        .document(myAmbulance.id)
                        .update(mapOf("organisation" to newOrg, "plateNumber" to newPlate))
                    showEditDialog = false
                }) { Text("Update") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this ambulance?") },
            confirmButton = {
                Button(
                    onClick = {
                        FirebaseFirestore.getInstance()
                            .collection("ambulances")
                            .document(myAmbulance.id)
                            .delete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun PendingRequestCard(req: sosRequests, context: Context, requestModel: RequestModel, myAmbulance: Ambulance?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2B2B).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFFFBFA))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Location: ${req.latitude}, ${req.longitude}",
                    color = Color.White.copy(alpha = 0.9f)
                    ,
                    modifier = Modifier.clickable {
                        openMaps( context,req.latitude, req.longitude)
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            myAmbulance?.let {
                Button(
                    onClick = {
                        requestModel.acceptRequest(req.id, it.id) { success, error ->
                            if (success) {
                                Toast.makeText(context, "✅ Request accepted", Toast.LENGTH_SHORT).show()
                                // 🔹 Open Google Maps with location
                                openMaps(context, req.latitude, req.longitude)
                            } else {
                                Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Accept Request", color = Color(0xFFFFFBFA)
                    )
                }

            }
        }
    }
}
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White.copy(alpha = 0.9f)
        ,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun EmptyCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2B2B).copy(alpha = 0.95f)

        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = Color(0xFFFFFBFA),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2B2B).copy(alpha = 0.95f)

        ),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Request ID: ${request.id}", fontWeight = FontWeight.Bold, color = Color(0xFFFFFBFA)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint =Color(0xFFFFFBFA)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Location: ${request.latitude}, ${request.longitude}",
                    color = Color.White.copy(alpha = 0.9f)
                    ,
                    modifier = Modifier.clickable {
                        openMaps( context,request.latitude, request.longitude)
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    onResolve()
                    Toast.makeText(context, "✅ Request resolved", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFBFA)
                )
            ) {
                Text("Mark as Resolved", color = Color(0xFFFFFBFA)
                )
            }
        }
    }
}

fun openMaps(context: Context, latitude: Double, longitude: Double) {
    try {
        // Create a geo URI
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(Ambulance+Location)")

        // Intent to open maps
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps") // Use Google Maps if installed
        }

        // Start activity
        context.startActivity(mapIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Unable to open Maps", Toast.LENGTH_SHORT).show()
    }
}

