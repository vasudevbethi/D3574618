package uk.ac.tees.mad.d3574618.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.auth.AuthDestination
import uk.ac.tees.mad.d3574618.auth.AuthScreen
import uk.ac.tees.mad.d3574618.auth.ForgotPasswordDestination
import uk.ac.tees.mad.d3574618.auth.ForgotPasswordScreen
import uk.ac.tees.mad.d3574618.auth.MoreDetailDestination
import uk.ac.tees.mad.d3574618.auth.MoreDetailsScreen
import uk.ac.tees.mad.d3574618.ui.screens.AddItemSuccess
import uk.ac.tees.mad.d3574618.ui.screens.AddItems
import uk.ac.tees.mad.d3574618.ui.screens.AddItemsDestination
import uk.ac.tees.mad.d3574618.ui.screens.AddItemsSuccessDestination
import uk.ac.tees.mad.d3574618.ui.screens.HomeScreen
import uk.ac.tees.mad.d3574618.ui.screens.HomeScreenDestination
import uk.ac.tees.mad.d3574618.ui.screens.HomeScreenDestination.route
import uk.ac.tees.mad.d3574618.ui.screens.ItemDetailsDestination
import uk.ac.tees.mad.d3574618.ui.screens.ItemDetailsScreen
import uk.ac.tees.mad.d3574618.ui.screens.LikedItemsDestination
import uk.ac.tees.mad.d3574618.ui.screens.LikedItemsScreen
import uk.ac.tees.mad.d3574618.ui.screens.ProfileDestination
import uk.ac.tees.mad.d3574618.ui.screens.ProfileScreen
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

    val initialDestination =
        if (currentUser != null) route else AuthDestination.route

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
                loginSuccess = {
                    navController.navigate(HomeScreenDestination.route)

                },
                registerSuccess = {
                    navController.navigate(MoreDetailDestination.route)

                },
                onForgetPassword = {
                    navController.navigate(ForgotPasswordDestination.route)
                }
            )
        }

        composable(MoreDetailDestination.route) {
            MoreDetailsScreen(onSuccess = {
                navController.navigate(HomeScreenDestination.route)
            })
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
            HomeScreen(
                navController = navController,
                onItemClick = {
                    navController.navigate("${ItemDetailsDestination.route}/$it")
                }, onLikedClick = {
                    navController.navigate(LikedItemsDestination.route)
                }
            )
        }

        composable(LikedItemsDestination.route) {
            LikedItemsScreen(
                onNavigateUp = {
                    navController.navigateUp()
                },
                onItemClick = {
                    navController.navigate("${ItemDetailsDestination.route}/$it")
                }
            )
        }

        composable(
            route = ItemDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ItemDetailsDestination.itemIdArg) {
                type = NavType.StringType
            })
        ) {
            ItemDetailsScreen(
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(AddItemsDestination.route) {
            AddItems(
                onAddItemSuccess = {
                    navController.popBackStack()
                    navController.navigate(AddItemsSuccessDestination.route + "/" + it)
                },
                onNavigateUp = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            AddItemsSuccessDestination.routeWithArgs,
            arguments = listOf(navArgument(ItemDetailsDestination.itemIdArg) {
                type = NavType.StringType
            })
        ) {
            val itemId = it.arguments?.getString("itemId")
            AddItemSuccess(
                onSuccess = {
                    navController.popBackStack()
                    navController.navigate(HomeScreenDestination.route)
                },
                onViewItem = {
                    navController.navigate(ItemDetailsDestination.route + "/" + itemId)
                }
            )
        }

        composable(ProfileDestination.route) {
            ProfileScreen(
                navController = navController, onLogOut = {
                    scope.launch {
                        firebase.signOut()
                        navController.navigate(AuthDestination.route)
                    }
                }, onItemClick = {
                    navController.navigate(ItemDetailsDestination.route + "/" + it)
                }
            )
        }
    }
}