package com.example.rapaid.data


import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SosHelper(private val context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val locationHelper = LocationHelper(context)

    fun sendSosRequest(onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(false)

        locationHelper.getLastLocation { location ->
            val sosData = hashMapOf(
                "userId" to userId,
                "timestamp" to System.currentTimeMillis(),
                "status" to "pending",
                "latitude" to location?.latitude,
                "longitude" to location?.longitude
            )

            firestore.collection("alerts")
                .add(sosData)
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        }
    }
}
