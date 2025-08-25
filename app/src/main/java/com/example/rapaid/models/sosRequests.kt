package com.example.rapaid.models

data class sosRequests(
    var id: String = "",
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val status: String = "pending", // pending, in_progress, resolved
    val assignedAmbulanceId: String? = null,
    val timestamp: Long = 0L
)
