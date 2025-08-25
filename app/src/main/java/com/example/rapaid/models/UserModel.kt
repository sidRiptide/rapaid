package com.example.rapaid.models

data class UserModel(
    val username: String = "",
    val fullname: String = "",
    val email: String = "",
    val userId: String = "",
    val role: String = "user" ,// default = normal user, can be "ambulance" or "admin"
    val plateNumber: String? = null,
    val organisation: String? = null
)
