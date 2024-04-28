package uk.ac.tees.mad.d3574618.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ControlPoint
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uk.ac.tees.mad.d3574618.ui.screens.AddItemsDestination
import uk.ac.tees.mad.d3574618.ui.screens.HomeScreenDestination
import uk.ac.tees.mad.d3574618.ui.screens.ProfileDestination

sealed class BottomNavigationScreens(
    val route: String,
    val selectedIcon: ImageVector,
    val nameRes: Int
) {
    object Home : BottomNavigationScreens(
        route = HomeScreenDestination.route,
        selectedIcon = Icons.Outlined.Home,
        nameRes = HomeScreenDestination.titleRes
    )
    object AddItem : BottomNavigationScreens(
        route = AddItemsDestination.route,
        selectedIcon = Icons.Outlined.ControlPoint,
        nameRes = AddItemsDestination.titleRes
    )


    object Profile : BottomNavigationScreens(
        route = ProfileDestination.route,
        selectedIcon = Icons.Outlined.Person,
        nameRes = ProfileDestination.titleRes
    )


}

val bottomNavigationItems = listOf(
    BottomNavigationScreens.Home,
    BottomNavigationScreens.AddItem,
    BottomNavigationScreens.Profile,
)

@Composable
fun BottomNavBar(
    tabBarItems: List<BottomNavigationScreens>,
    navController: NavController,
    selectedTabIndex: Int,
    onAddItemClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .background(Color.White),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            NavItem(
                selected = selectedTabIndex == 0,
                onClick = {
                    navController.navigate(tabBarItems[0].route)
                },
                tabBarItem = tabBarItems[0],
                interactionSource = interactionSource
            )


            Column(
                Modifier
                    .clickable(interactionSource = interactionSource, indication = null) {
                        onAddItemClick()
                    }
                    .height(70.dp), verticalArrangement = Arrangement.Center
            ) {
                Column(
                    Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = tabBarItems[1].selectedIcon,
                        contentDescription = tabBarItems[1].route,
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }


            NavItem(
                selected = selectedTabIndex == 2,
                onClick = {
                    navController.navigate(tabBarItems[2].route)
                },
                tabBarItem = tabBarItems[2],
                interactionSource = interactionSource
            )

        }
    }
}

@Composable
fun NavItem(
    selected: Boolean,
    onClick: () -> Unit,
    tabBarItem: BottomNavigationScreens,
    interactionSource: MutableInteractionSource
) {

    Column(
        Modifier
            .clickable(interactionSource = interactionSource, indication = null) {
                onClick()
            }
            .height(70.dp)

    ) {
        Column(
            Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = tabBarItem.selectedIcon,
                contentDescription = tabBarItem.route,
                tint = if (selected) MaterialTheme.colorScheme.primary else Color.Black,
                modifier = Modifier.size(25.dp)
            )

            Text(
                text = stringResource(id = tabBarItem.nameRes),
                fontSize = 14.sp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Black
            )
        }
    }
}