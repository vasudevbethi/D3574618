package uk.ac.tees.mad.d3574618.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.auth.AuthDestination
import uk.ac.tees.mad.d3574618.auth.AuthScreen
import uk.ac.tees.mad.d3574618.auth.ForgotPasswordDestination
import uk.ac.tees.mad.d3574618.auth.ForgotPasswordScreen
import uk.ac.tees.mad.d3574618.auth.GoogleAuthUiClient
import uk.ac.tees.mad.d3574618.ui.screens.HomeScreen
import uk.ac.tees.mad.d3574618.ui.screens.HomeScreenDestination
import uk.ac.tees.mad.d3574618.ui.screens.SplashScreen
import uk.ac.tees.mad.d3574618.ui.screens.SplashScreenDestination

@Composable
fun ReusableItemNavigation() {
    // Create a NavHostController to manage navigation within the app
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val firebase = FirebaseAuth.getInstance()
    val currentUser = firebase.currentUser
    val context = LocalContext.current

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            oneTapClient = Identity.getSignInClient(context)
        )
    }
    val initialDestination =
        if ((currentUser != null) || (googleAuthUiClient.getSignedInUser() != null)) HomeScreenDestination.route else AuthDestination.route

    // Define the navigation graph using NavHost
    NavHost(navController = navController, startDestination = SplashScreenDestination.route) {
        // Splash screen destination
        composable(SplashScreenDestination.route) {
            // Display the splash screen composable
            SplashScreen(onFinish = {
                scope.launch(Dispatchers.Main) {
                    navController.popBackStack() // Clear any existing back stack
                    navController.navigate(initialDestination) // Navigate to the home screen
                }
            })
        }

        composable(AuthDestination.route) {
            AuthScreen(
                registerSuccess = {
                    navController.navigate(HomeScreenDestination.route)
                },
                onForgetPassword = {
                    navController.navigate(ForgotPasswordDestination.route)
                }
            )
        }

        composable(ForgotPasswordDestination.route) {
            ForgotPasswordScreen(
                onNavigateUp = {
                    navController.popBackStack()
                },
                onEmailSent = {
                    navController.navigate(AuthDestination.route)
                }
            )
        }

        // Home screen destination
        composable(HomeScreenDestination.route) {
            // Display the home screen composable
            HomeScreen(navController = navController, onLogOut = {
                scope.launch {
                    firebase.signOut()
                    googleAuthUiClient.signOut()
                    navController.navigate(SplashScreenDestination.route)
                }
            })
        }
    }
}