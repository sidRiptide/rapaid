package com.example.rapaid.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.rapaid.models.Ambulance
import com.example.rapaid.models.sosRequests
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class RequestModel {

    private val firestore = FirebaseFirestore.getInstance()

    // ✅ Send SOS request
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ✅ Automatically fetch userId from FirebaseAuth
    fun sendSOS(
        location: Location,
        onResult: (Boolean, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onResult(false, "User not logged in")
            return
        }

        val sos = hashMapOf(
            "userId" to userId,
            "status" to "pending",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("sos_requests")
            .add(sos)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // ✅ Listen for all SOS requests (for dashboard)
    // inside RequestModel.kt
    fun listenForSOSRequests(onUpdate: (List<sosRequests>) -> Unit): ListenerRegistration {
        val db = FirebaseFirestore.getInstance()

        return db.collection("sos_requests")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    onUpdate(emptyList()) // error → return empty list
                    return@addSnapshotListener
                }

                val requests = snapshots?.documents?.mapNotNull { doc ->
                    try {
                        sosRequests(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0,
                            status = doc.getString("status") ?: "pending",
                            assignedAmbulanceId = doc.getString("assignedAmbulanceId")
                        )
                    } catch (_: Exception) {
                        null
                    }
                } ?: emptyList()

                onUpdate(requests)
            }
    }


    // ✅ Listen for SOS status for a specific user
    fun listenToSOSStatus(
        onStatusChange: (String) -> Unit
    ): ListenerRegistration? {
        val userId = auth.currentUser?.uid ?: return null

        return db.collection("sos_requests")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    onStatusChange("error: ${e.message}")
                    return@addSnapshotListener
                }

                val status = snapshots?.documents?.firstOrNull()?.getString("status") ?: "unknown"
                onStatusChange(status)
            }
    }


    // ✅ Mark request resolved (for ambulance side)
    fun markRequestResolved(requestId: String, context: Context) {
        FirebaseFirestore.getInstance()
            .collection("sos_requests")
            .document(requestId)
            .update("status", "resolved")
            .addOnSuccessListener {
                Toast.makeText(context, "Request resolved ✅", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // inside RequestModel.kt
//    fun assignAmbulance(requestId: String, ambulanceId: String, context: Context) {
//        val db = FirebaseFirestore.getInstance()
//
//        // Step 1: Update the SOS request with the assigned ambulance
//        db.collection("sosRequests").document(requestId)
//            .update(
//                mapOf(
//                    "status" to "in_progress",
//                    "assignedAmbulanceId" to ambulanceId
//                )
//            )
//            .addOnSuccessListener {
//                // Step 2: Mark the ambulance as Busy
//                db.collection("ambulances").document(ambulanceId)
//                    .update("status", "Busy")
//                    .addOnSuccessListener {
//                        Toast.makeText(context, "🚑 Ambulance assigned successfully", Toast.LENGTH_SHORT).show()
//                    }
//                    .addOnFailureListener { e ->
//                        Toast.makeText(context, "Failed to update ambulance: ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(context, "Failed to assign ambulance: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
    // inside RequestModel.kt
    fun listenForAmbulances(onUpdate: (List<Ambulance>) -> Unit): ListenerRegistration {
        val db = FirebaseFirestore.getInstance()

        return db.collection("ambulances")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    onUpdate(emptyList()) // In case of error, return empty list
                    return@addSnapshotListener
                }

                val ambulances = snapshots?.documents?.mapNotNull { doc ->
                    try {
                        Ambulance(
                            id = doc.id,
                            organisation = doc.getString("organisation") ?: "Unknown Org",
                            driver = doc.getString("driver") ?: "Unnamed Driver",
                            plateNumber = doc.getString("plateNumber") ?: "No Plate",
                            status = doc.getString("status") ?: "Unavailable",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0
                        )
                    } catch (_: Exception) {
                        null
                    }
                } ?: emptyList()

                onUpdate(ambulances)
            }
    }
//    fun autoRegisterCurrentUserAsAmbulance() {
//        val user = FirebaseAuth.getInstance().currentUser ?: return
//        val db = FirebaseFirestore.getInstance()
//
//        val ambulance = Ambulance(
//            id = user.uid,
//            driver = user.email ?: "Unknown Driver",
//            status = "Available"
//        )
//
//        db.collection("ambulances").document(user.uid).set(ambulance)
//    }
//fun autoRegisterCurrentUserAsAmbulance() {
//    val currentUser = FirebaseAuth.getInstance().currentUser ?: return
//    val db = FirebaseFirestore.getInstance()
//    val ambulanceRef = db.collection("ambulances").document(currentUser.uid)
//
//    // Check if ambulance already exists
//    ambulanceRef.get().addOnSuccessListener { document ->
//        if (!document.exists()) {
//            // Ambulance doesn't exist → create new
//            val newAmbulance = Ambulance(
//                id = currentUser.uid,
//                driver = currentUser.displayName ?: "Unnamed Driver",
//                status = "Available"
//            )
//
//            ambulanceRef.set(newAmbulance)
//                .addOnSuccessListener {
//                    Log.d("RequestModel", "Ambulance registered successfully")
//                }
//                .addOnFailureListener { e ->
//                    Log.e("RequestModel", "Failed to register ambulance: ${e.message}")
//                }
//        } else {
//            Log.d("RequestModel", "Ambulance already registered")
//        }
//    }.addOnFailureListener { e ->
//        Log.e("RequestModel", "Failed to check ambulance existence: ${e.message}")
//    }
//}
//fun autoRegisterCurrentUserAsAmbulance(organisation: String, plateNumber: String) {
//    val userId = auth.currentUser?.uid ?: return
//
//    val ambulance = Ambulance(
//        id = userId,
//        organisation = organisation,       // ✅ use actual driver name
//        plateNumber = plateNumber, // ✅ save plate number
//        latitude = 0.0,
//        longitude = 0.0,
//        status = "Available"
//    )
//
//    firestore.collection("ambulances")
//        .document(userId)
//        .set(ambulance)
//        .addOnSuccessListener {
//            println("✅ Ambulance registered successfully")
//        }
//        .addOnFailureListener { e ->
//            println("❌ Failed to register ambulance: ${e.message}")
//        }
//}
fun autoRegisterCurrentUserAsAmbulance(
    organisation: String,
    plateNumber: String
) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: return
    val driverName = auth.currentUser?.displayName ?: ""   // try name first
    val driverEmail = auth.currentUser?.email ?: "Unnamed Driver"

    val ambulance = hashMapOf(
        "id" to userId,
        "driver" to if (driverName.isNotEmpty()) driverName else driverEmail,
        "organisation" to organisation,
        "plateNumber" to plateNumber,
        "status" to "Available",
        "latitude" to 0.0,
        "longitude" to 0.0
    )

    FirebaseFirestore.getInstance()
        .collection("ambulances") // 👈 make sure you always use the same collection name
        .document(userId)
        .set(ambulance)
        .addOnSuccessListener {
            Log.d("Firestore", "✅ Ambulance registered successfully")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "❌ Failed to register ambulance: ${e.message}")
        }
}


    fun acceptRequest(requestId: String, ambulanceId: String, onResult: (Boolean, String?) -> Unit) {
        val requestRef = FirebaseFirestore.getInstance().collection("sos_requests").document(requestId)

        requestRef.update(
            mapOf(
                "status" to "accepted",
                "assignedAmbulanceId" to ambulanceId
            )
        ).addOnSuccessListener {
            onResult(true, null)
        }.addOnFailureListener { e ->
            onResult(false, e.message)
        }

    }




}
