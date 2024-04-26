package uk.ac.tees.mad.d3574618.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination

object AddItemsDestination : NavigationDestination {
    override val route = "add_item"
    override val titleRes: Int = R.string.add_item
}


@Composable
fun AddItems() {
    Text(text = "Add item")
}