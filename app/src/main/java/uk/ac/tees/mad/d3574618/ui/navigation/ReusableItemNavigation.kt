package uk.ac.tees.mad.d3574618.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.d3574618.ui.screens.HomeScreen
import uk.ac.tees.mad.d3574618.ui.screens.HomeScreenDestination
import uk.ac.tees.mad.d3574618.ui.screens.SplashScreen
import uk.ac.tees.mad.d3574618.ui.screens.SplashScreenDestination

@Composable
fun ReusableItemNavigation() {
    // Create a NavHostController to manage navigation within the app
    val navController = rememberNavController()

    // Define the navigation graph using NavHost
    NavHost(navController = navController, startDestination = SplashScreenDestination.route) {
        // Splash screen destination
        composable(SplashScreenDestination.route) {
            // Display the splash screen composable
            SplashScreen(navController = (navController))
        }
        // Home screen destination
        composable(HomeScreenDestination.route) {
            // Display the home screen composable
            HomeScreen(navController = navController)
        }
    }
}