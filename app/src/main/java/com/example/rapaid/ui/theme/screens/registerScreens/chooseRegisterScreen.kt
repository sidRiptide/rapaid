package com.example.rapaid.ui.theme.screens.registerScreens


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rapaid.R
import com.example.rapaid.navigation.ROUTE_REGISTER_PATIENT
import com.example.rapaid.navigation.ROUTE_REGISTER_AMBULANCE

@Composable
fun ChooseRegisterScreen(navController: NavController) {
    Box {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "registerBackground",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Register as",
            fontSize = 40.sp,
            fontFamily = FontFamily.SansSerif,
            fontStyle = FontStyle.Normal,
            color = Color.Blue,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { navController.navigate(ROUTE_REGISTER_PATIENT) },
            colors = ButtonDefaults.buttonColors(Color.Blue),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Patient", fontSize = 20.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate(ROUTE_REGISTER_AMBULANCE) },
            colors = ButtonDefaults.buttonColors(Color.Blue),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Ambulance", fontSize = 20.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            "Already have an account? Login",
            style = TextStyle(color = Color.Blue, fontSize = 16.sp),
            modifier = Modifier.clickable { navController.navigate("login") }
        )
    }
}
