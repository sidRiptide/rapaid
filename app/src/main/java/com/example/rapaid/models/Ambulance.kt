package com.example.rapaid.models
//
//data class Ambulance(
//
//    var id: String = "",
//    val driver: String = "",
//    val latitude: Double = 0.0,
//    val longitude: Double = 0.0,
//    val status: String = "Available",// Available, Busy, Offline
//    val organisation: String = "",
//    val plateNumber: String = "",
//
//)
data class Ambulance(
    val id: String = "",
    val organisation: String = "",
    val driver: String = "",
    val plateNumber: String = "",
    val status: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
