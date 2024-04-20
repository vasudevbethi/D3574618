package uk.ac.tees.mad.d3574618.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
    override val titleRes: Int = R.string.app_name
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    onLogOut: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}