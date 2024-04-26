package uk.ac.tees.mad.d3574618.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.ui.navigation.BottomNavBar
import uk.ac.tees.mad.d3574618.ui.navigation.BottomNavigationScreens
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination
import uk.ac.tees.mad.d3574618.ui.navigation.bottomNavigationItems

object ProfileDestination : NavigationDestination {
    override val route = "profile"
    override val titleRes: Int = R.string.profile
}

@Composable
fun ProfileScreen(
    navController: NavHostController,
    onLogOut: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                tabBarItems = bottomNavigationItems,
                navController = navController,
                selectedTabIndex = bottomNavigationItems.indexOf(BottomNavigationScreens.Profile),
                onAddItemClick = {
                    navController.navigate(AddItemsDestination.route)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Greeting("Android")
            Button(
                onClick = onLogOut
            ) {
                Text(text = "Logout")
            }
        }
    }
}