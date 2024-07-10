package uk.ac.tees.mad.d3574618.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import uk.ac.tees.mad.d3574618.ui.screens.AddItemsDestination
import uk.ac.tees.mad.d3574618.ui.screens.HomeScreenDestination
import uk.ac.tees.mad.d3574618.ui.screens.ProfileDestination
import uk.ac.tees.mad.d3574618.ui.theme.primaryGreen

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
    Box(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(),
    ) {
        Column(
            Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(interactionSource = interactionSource, indication = null) {
                    onAddItemClick()
                }.padding(4.dp)
                .height(80.dp)
                ,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = tabBarItems[1].selectedIcon,
                contentDescription = tabBarItems[1].route,
                tint = primaryGreen,
                modifier = Modifier
                    .size(80.dp)
            )
        }
        Row(
            modifier = Modifier
                .border(1.dp, Color.LightGray, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem(
                selected = selectedTabIndex == 0,
                onClick = {
                    navController.navigate(tabBarItems[0].route)
                },
                tabBarItem = tabBarItems[0],
                interactionSource = interactionSource
            )



            Spacer(modifier = Modifier.width(30.dp))


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
                tint = if (selected) primaryGreen else Color.Black,
                modifier = Modifier.size(25.dp)
            )

            Text(
                text = stringResource(id = tabBarItem.nameRes),
                fontSize = 14.sp,
                color = if (selected) primaryGreen else Color.Black
            )
        }
    }
}