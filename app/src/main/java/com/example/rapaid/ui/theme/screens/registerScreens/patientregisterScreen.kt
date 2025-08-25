package com.example.rapaid.ui.theme.screens.registerScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rapaid.R
import com.example.rapaid.data.AuthViewModel
import com.example.rapaid.navigation.ROUTE_LOGIN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    Box {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "registerBackground",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Register as Patient",
            fontSize = 30.sp,
            fontFamily = FontFamily.SansSerif,
            color = Color.Blue,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.rapaid),
            contentDescription = "Logo",
            modifier = Modifier.fillMaxWidth().height(80.dp),
            contentScale = ContentScale.Fit
        )

        OutlinedTextField(
            value = username, onValueChange = { username = it },
            label = { Text("Enter Username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.8f),
            textStyle = TextStyle(color = Color.Blue)
        )

        OutlinedTextField(
            value = fullname, onValueChange = { fullname = it },
            label = { Text("Enter Fullname") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.8f),
            textStyle = TextStyle(color = Color.Blue)
        )

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Enter Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.8f),
            textStyle = TextStyle(color = Color.Blue)
        )

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Enter Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.8f),
            textStyle = TextStyle(color = Color.Blue)
        )

        OutlinedTextField(
            value = confirmPassword, onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.8f),
            textStyle = TextStyle(color = Color.Blue)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                authViewModel.signupUser(
                    username, fullname, email, password, confirmPassword,
                    navController, context
                )
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(Color.Blue)
        ) {
            Text("Register", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Already registered? Login here",
            modifier = Modifier.clickable { navController.navigate(ROUTE_LOGIN) },
            color = Color.Blue
        )
    }
}
