package com.example.rapaid.navigation



import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rapaid.data.Location
import com.example.rapaid.ui.theme.screens.AmbulanceDashboard
import com.example.rapaid.ui.theme.screens.HomeScreen
import com.example.rapaid.ui.theme.screens.LocationPermissionScreen

import com.example.rapaid.ui.theme.screens.UserSOSScreen
import com.example.rapaid.ui.theme.screens.SosStatusScreen
import com.example.rapaid.ui.theme.screens.SplashScreen
import com.example.rapaid.ui.theme.screens.UserSOSScreen
import com.example.rapaid.ui.theme.screens.loginScreen
import com.example.rapaid.ui.theme.screens.registerScreens.AmbulanceRegisterScreen
import com.example.rapaid.ui.theme.screens.registerScreens.ChooseRegisterScreen
import com.example.rapaid.ui.theme.screens.registerScreens.PatientRegisterScreen
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(ROUTE_SPLASH) { SplashScreen { navController.navigate(ROUTE_CHOOSE_REGISTER){popUpTo(
            ROUTE_SPLASH){inclusive=true}} }  }
        composable(ROUTE_LOCATION_PERMISSION) { LocationPermissionScreen(navController) }
        composable(ROUTE_CHOOSE_REGISTER){ChooseRegisterScreen(navController)}
        composable(ROUTE_REGISTER_AMBULANCE){AmbulanceRegisterScreen(navController)}
        composable(ROUTE_REGISTER_PATIENT){PatientRegisterScreen(navController)}
        composable(ROUTE_HOME) {
            HomeScreen(navController)
        }


        composable(ROUTE_SOS_SCREEN) {
            // Pass dummy coordinates for now; replace with actual location
            UserSOSScreen(
                navController = navController,

            )
        }
        composable(
            route = "${ROUTE_SOS_SCREEN}/{userId}/{location}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("location") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
//            val location = backStackEntry.arguments?.getString("location") ?: ""
            val location = Location(1.2921, 36.8219)
            SosStatusScreen(userId = userId, location )
        }
        composable(ROUTE_AMBULANCE_DASHBOARD) {
            AmbulanceDashboard(
                navController = navController,
                context = LocalContext.current
            )
        }
        composable(ROUTE_LOGIN){loginScreen(navController)}
//        composable(ROUTE_REGISTER){ RegisterScreen(navController)}
        composable(
            route = "$ROUTE_SOS_STATUS/{userId}/{lat}/{lng}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lng = backStackEntry.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0

            val location = Location(lat, lng)

            SosStatusScreen(
                userId = userId,
                location = location
            )
        }



    }

}
