package com.negarfahmifaishal.bengkeldewe.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object AddBooking : Screen("add_booking")
    object EditBooking : Screen("edit_booking/{id}") {
        fun createRoute(id: String) = "edit_booking/$id"
    }
    object Profile : Screen("profile")
}
