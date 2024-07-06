package uk.ac.tees.mad.d3574618.ui.screens

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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.data.domain.FirestoreItemResponse
import uk.ac.tees.mad.d3574618.data.domain.ItemCategory
import uk.ac.tees.mad.d3574618.data.domain.toItem
import uk.ac.tees.mad.d3574618.showToast
import uk.ac.tees.mad.d3574618.ui.components.GridItem
import uk.ac.tees.mad.d3574618.ui.navigation.BottomNavBar
import uk.ac.tees.mad.d3574618.ui.navigation.BottomNavigationScreens
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination
import uk.ac.tees.mad.d3574618.ui.navigation.bottomNavigationItems
import uk.ac.tees.mad.d3574618.ui.theme.primaryGreen
import uk.ac.tees.mad.d3574618.ui.viewmodels.HomeViewModel

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
    override val titleRes: Int = R.string.home
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    onItemClick: (String) -> Unit,
    onLikedClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var selectedCategoryIndex = remember {
        mutableIntStateOf(0)
    }
    val itemRetrieveState = viewModel.itemsList.collectAsState(initial = null)

    val itemListSuccess = itemRetrieveState.value?.isSuccess
    val context = LocalContext.current

    val itemList = remember {
        mutableStateOf(emptyList<FirestoreItemResponse>())
    }

    LaunchedEffect(itemRetrieveState.value?.isSuccess) {
        if (!itemRetrieveState.value?.isSuccess.isNullOrEmpty()) {
            itemRetrieveState.value?.isSuccess.let {
                itemList.value = it?.filter { item ->
                    item.item?.keywords?.contains("") == true && item.item.itemSwapStatus != "Swapped"
                }!!
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.getItemList()
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
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorites",
                        Modifier
                            .size(26.dp)
                            .clickable {
                                onLikedClick()
                            }
                            .align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(20.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(ItemCategory.entries) { index, item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (selectedCategoryIndex.intValue == index)
                                    primaryGreen
                                else Color.LightGray
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable {
                                selectedCategoryIndex.intValue = index
                                if (index == 0) {
                                    itemRetrieveState.value?.isSuccess?.let {
                                        itemList.value = it
                                    }
                                } else {

                                    itemRetrieveState.value?.isSuccess?.let {
                                        itemList.value = it.filter { i ->
                                            i.item?.category == item
                                        }
                                    }
                                }
                            },
                    ) {
                        Text(
                            text = item.name,
                            color = if (selectedCategoryIndex.intValue == index)
                                Color.White
                            else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            SearchBar(
                onChange = { text ->
                    itemRetrieveState.value?.isSuccess?.let {
                        itemList.value = it.filter { i ->
                            i.item?.keywords?.contains(text) == true
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (itemListSuccess.isNullOrEmpty()) {
                Box(modifier = Modifier.weight(1f)) {
                    CircularProgressIndicator()
                }
            } else {
                if (itemList.value.isEmpty()) {
                    Text(text = "No items")
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(itemList.value) { item ->
                            GridItem(
                                item.toItem(),
                                onClick = {
                                    onItemClick(item.key!!)

                                }, onLike = {
                                    viewModel.addItemToFavorite(item.toItem())
                                    context.showToast("Item Liked")
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(70.dp))
        }
    }

}


@Composable
fun SearchBar(
    onChange: (String) -> Unit
) {
    var textFieldValue by remember {
        mutableStateOf("")
    }
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            onChange(it)
        },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(imageVector = Icons.Outlined.Search, contentDescription = "", tint = Color.Gray)
        },
        placeholder = {
            Text(text = "Search for anything", color = Color.Gray)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.LightGray,
            unfocusedBorderColor = Color.Black,
            unfocusedContainerColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}