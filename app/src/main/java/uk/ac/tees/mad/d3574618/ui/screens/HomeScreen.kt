package uk.ac.tees.mad.d3574618.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.ui.navigation.BottomNavBar
import uk.ac.tees.mad.d3574618.ui.navigation.BottomNavigationScreens
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination
import uk.ac.tees.mad.d3574618.ui.navigation.bottomNavigationItems

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
    override val titleRes: Int = R.string.home
}

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    var selectedCategoryIndex = remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                tabBarItems = bottomNavigationItems,
                navController = navController,
                selectedTabIndex = bottomNavigationItems.indexOf(BottomNavigationScreens.Home),
                onAddItemClick = {
                    navController.navigate(AddItemsDestination.route)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(vertical = innerPadding.calculateTopPadding(), horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(Modifier.height(40.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        Modifier
                            .size(26.dp)
                            .align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(20.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(categories) { index, item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (selectedCategoryIndex.intValue == index)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable {
                                selectedCategoryIndex.intValue = index
                            },
                    ) {
                        Text(
                            text = item,
                            color = if (selectedCategoryIndex.intValue == index)
                                Color.White
                            else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            SearchBar()
            Spacer(modifier = Modifier.height(20.dp))
            ItemsGrid(gridItems)
        }
    }

}

@Composable
fun ItemsGrid(gridItems: List<Item>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(gridItems) { item ->
            GridItem(item)
        }
    }
}

@Composable
fun GridItem(item: Item) {
    Column(Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .clip(RoundedCornerShape(24.dp))
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Row(Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Outlined.FavoriteBorder, contentDescription = "Like")
                }
            }
        }
        Column(Modifier.padding(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Swap",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Outlined.SwapHoriz,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = item.category, fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = item.category, fontSize = 13.sp, color = Color.Gray)
        }

    }

}

@Composable
fun SearchBar() {
    var textFieldValue = remember {
        TextFieldValue()
    }
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
        },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(imageVector = Icons.Outlined.Search, contentDescription = "", tint = Color.Gray)
        },
        placeholder = {
            Text(text = "Search for anything", color = Color.Gray)
        },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = Color.Transparent,
            disabledContainerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(24.dp)
    )
}

val categories = listOf(
    "All", "Electronics", "Clothes", "Home", "Furniture", "Cosmetics"
)

data class Item(
    val id: Int,
    val name: String,
    val category: String,
    val imageRes: Int
)

val gridItems = listOf(
    Item(
        id = 1,
        name = "Iphone 12 Pro Max",
        category = "Electronics",
        imageRes = R.drawable.iphone
    ),
    Item(
        id = 2,
        name = "Airpods Pro",
        category = "Electronics",
        imageRes = R.drawable.airpods
    ),
    Item(
        id = 3,
        name = "Nike Men's Sneakers",
        category = "Electronics",
        imageRes = R.drawable.shoes
    ),
    Item(
        id = 4,
        name = "Play Station 5",
        category = "Electronics",
        imageRes = R.drawable.ps5
    ),
    Item(
        id = 5,
        name = "Macbook Pro",
        category = "Electronics",
        imageRes = R.drawable.laptop
    )
)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}