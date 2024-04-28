package uk.ac.tees.mad.d3574618

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.d3574618.auth.MoreDetailsScreen
import uk.ac.tees.mad.d3574618.ui.navigation.ReusableItemNavigation
import uk.ac.tees.mad.d3574618.ui.theme.ReusableItemExchangeTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReusableItemExchangeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ReusableItemNavigation()


                }
            }
        }
    }
}