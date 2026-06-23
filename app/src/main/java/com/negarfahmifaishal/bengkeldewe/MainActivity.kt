package com.negarfahmifaishal.bengkeldewe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.negarfahmifaishal.bengkeldewe.datastore.AuthPreferences
import com.negarfahmifaishal.bengkeldewe.ui.navigation.Screen
import com.negarfahmifaishal.bengkeldewe.ui.screens.add_booking.AddBookingScreen
import com.negarfahmifaishal.bengkeldewe.ui.screens.edit_booking.EditBookingScreen
import com.negarfahmifaishal.bengkeldewe.ui.screens.home.HomeScreen
import com.negarfahmifaishal.bengkeldewe.ui.screens.home.HomeViewModel
import com.negarfahmifaishal.bengkeldewe.ui.screens.login.LoginScreen
import com.negarfahmifaishal.bengkeldewe.ui.screens.profile.ProfileScreen
import com.negarfahmifaishal.bengkeldewe.ui.theme.BengkeldeweTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BengkeldeweTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BengkelDeweApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BengkelDeweApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val authPreferences = remember { AuthPreferences(context) }
    val isLoggedIn by authPreferences.isLoggedInFlow.collectAsState(initial = null)

    // Wait until login preference is loaded from DataStore
    if (isLoggedIn == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()
    val startDestination = if (isLoggedIn == true) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                modifier = modifier
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onAddClick = {
                    navController.navigate(Screen.AddBooking.route)
                },
                onEditClick = { id ->
                    navController.navigate(Screen.EditBooking.createRoute(id))
                },
                onDeleteClick = { id ->
                    // Handled inside HomeScreen Dialog
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                modifier = modifier
            )
        }
        composable(Screen.AddBooking.route) {
            AddBookingScreen(
                onNavigateBack = {
                    homeViewModel.getBookings() // Auto-refresh list
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
        composable(
            route = Screen.EditBooking.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            EditBookingScreen(
                bookingId = id,
                onNavigateBack = {
                    homeViewModel.getBookings() // Auto-refresh list
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                modifier = modifier
            )
        }
    }
}
