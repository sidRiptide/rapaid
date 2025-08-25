package com.example.rapaid.data

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.rapaid.models.UserModel
import com.example.rapaid.navigation.ROUTE_AMBULANCE_DASHBOARD
import com.example.rapaid.navigation.ROUTE_SOS_SCREEN
import com.example.rapaid.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val requestModel = RequestModel()

    // 🚨 User Signup
    fun signupUser(
        username: String,
        fullname: String,
        email: String,
        password: String,
        confirmPassword: String,
        navController: NavController,
        context: Context
    ) {
        if (username.isBlank() || email.isBlank() || fullname.isBlank() ||
            password.isBlank() || confirmPassword.isBlank()
        ) {
            Toast.makeText(context, "Please fill all the fields!", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_LONG).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val user = UserModel(
                        username = username,
                        fullname = fullname,
                        email = email,
                        userId = userId,
                        role = "User"
                    )

                    saveUserToFirestore(user, context) {
                        Toast.makeText(context, "User registered successfully", Toast.LENGTH_LONG).show()
                        navController.navigate(ROUTE_LOGIN) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                } else {
                    Toast.makeText(context, task.exception?.message ?: "Registration failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    // 🚑 Ambulance Signup
    fun signupAmbulance(
        email: String,
        password: String,
        confirmPassword: String,
        organisation: String,
        plateNumber: String,
        navController: NavController,
        context: Context
    ) {
        if ( email.isBlank()  || password.isBlank()
            || confirmPassword.isBlank() ||
            organisation.isBlank() || plateNumber.isBlank()
        ) {
            Toast.makeText(context, "Please fill all the fields!", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_LONG).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""

                    // Save both user + ambulance details
                    val ambulance = UserModel(

                        email = email,
                        userId = userId,
                        role = "ambulances",
                        organisation = organisation,
                        plateNumber = plateNumber
                    )

                    saveUserToFirestore(ambulance, context) {
                        requestModel.autoRegisterCurrentUserAsAmbulance(organisation,plateNumber)
                        Toast.makeText(context, "Ambulance registered successfully", Toast.LENGTH_LONG).show()
                        navController.navigate(ROUTE_LOGIN) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                } else {
                    Toast.makeText(context, task.exception?.message ?: "Registration failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    // 🔹 Save to Firestore
    private fun saveUserToFirestore(
        user: UserModel,
        context: Context,
        onSuccess: () -> Unit
    ) {
        firestore.collection("Users")
            .document(user.userId)
            .set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    Toast.makeText(context, task.exception?.message ?: "Failed to save User", Toast.LENGTH_LONG).show()
                }
            }
    }

    // 🚨 Login (unchanged, just uses role from Firestore)
    fun login(
        email: String,
        password: String,
        navController: NavController,
        context: Context
    ) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Email and password required!", Toast.LENGTH_LONG).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: ""

                firestore.collection("Users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val role = document.getString("role") ?: "User"
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()

                        if (role == "ambulances") {
                            navController.navigate(ROUTE_AMBULANCE_DASHBOARD) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        } else {
                            navController.navigate(ROUTE_SOS_SCREEN) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to fetch user role", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(context, task.exception?.message ?: "Login failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 🚨 Logout
    fun logout(context: Context, navController: NavController) {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()

        navController.navigate(ROUTE_LOGIN) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }
}

//FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
//            .addOnSuccessListener {
//                requestModel.autoRegisterCurrentUserAsAmbulance()
//                navController.navigate("ambulance_dashboard")
//            }
//            .addOnFailureListener { e ->
//                // handle error
//            }
