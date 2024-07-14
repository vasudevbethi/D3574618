package uk.ac.tees.mad.d3574618.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVerticalCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.data.domain.Item
import uk.ac.tees.mad.d3574618.data.domain.toItem
import uk.ac.tees.mad.d3574618.formattedDate
import uk.ac.tees.mad.d3574618.showToast
import uk.ac.tees.mad.d3574618.ui.components.GridItem
import uk.ac.tees.mad.d3574618.ui.navigation.BottomNavBar
import uk.ac.tees.mad.d3574618.ui.navigation.BottomNavigationScreens
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination
import uk.ac.tees.mad.d3574618.ui.navigation.bottomNavigationItems
import uk.ac.tees.mad.d3574618.ui.theme.primaryGreen
import uk.ac.tees.mad.d3574618.ui.viewmodels.MyListedItemsState
import uk.ac.tees.mad.d3574618.ui.viewmodels.ProfileUiState
import uk.ac.tees.mad.d3574618.ui.viewmodels.ProfileViewModel
import uk.ac.tees.mad.d3574618.ui.viewmodels.RequestState

object ProfileDestination : NavigationDestination {
    override val route = "profile"
    override val titleRes: Int = R.string.profile
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    onLogOut: () -> Unit,
    onItemClick: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val myListedItemsState = viewModel.myListedItemsState.collectAsState(initial = null)
    val userDetailsState = viewModel.currentUserData.collectAsState(initial = null)
    val acceptRequestState = viewModel.acceptSwapState.collectAsState(initial = null)
    val rejectRequestState = viewModel.rejectSwapState.collectAsState(initial = null)
    val deleteItemState = viewModel.deleteItemState.collectAsState(initial = null)

    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val tabList = listOf("Swap requests", "Listed Items")
    val swapRequestList = remember {
        mutableStateListOf<Pair<Item, Item>>()
    }

    val pagerState = rememberPagerState {
        tabList.size
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(myListedItemsState.value?.isSuccess) {
        if (!myListedItemsState.value?.isSuccess.isNullOrEmpty()) {
            myListedItemsState.value?.isSuccess?.forEach { response ->
                response.item?.swapRequests?.filter {
                    it.swapStatus == "Pending"
                }?.forEach {

                    swapRequestList.add(Pair(response.toItem(), it.toItem()))
                }
            }
        }
    }

    LaunchedEffect(userDetailsState.value?.data) {
        if (userDetailsState.value?.data != null) {
            userDetailsState.value?.data.let {
                viewModel.updateUiState(
                    ProfileUiState(
                        name = it?.item?.name!!,
                        email = it.item.email,
                        phone = it.item.phone,
                        imageUrl = it.item.profileImage,
                        location = it.item.location
                    )
                )
            }
        }
    }
    LaunchedEffect(deleteItemState.value?.data) {
        if (!deleteItemState.value?.data.isNullOrEmpty()) {
            context.showToast("Item deleted")
            viewModel.getMyListedItems()
        }
    }
    LaunchedEffect(deleteItemState.value?.error) {
        if (!deleteItemState.value?.error.isNullOrEmpty()) {
            context.showToast("Item delete Error: ${deleteItemState.value?.error}")
        }
    }

    LaunchedEffect(acceptRequestState.value?.data) {
        if (!acceptRequestState.value?.data.isNullOrEmpty()) {

            context.showToast("Accepted Request")
            viewModel.getMyListedItems()
        }
    }

    LaunchedEffect(acceptRequestState.value?.error) {
        if (!acceptRequestState.value?.error.isNullOrEmpty()) {
            context.showToast("Accepted Request Error: ${acceptRequestState.value?.error}")
        }
    }

    LaunchedEffect(rejectRequestState.value?.data) {
        if (!rejectRequestState.value?.data.isNullOrEmpty()) {
            viewModel.getMyListedItems()

            context.showToast("Request Rejected")
        }
    }

    LaunchedEffect(rejectRequestState.value?.error) {
        if (!rejectRequestState.value?.error.isNullOrEmpty()) {
            context.showToast("Rejected Request Error: ${rejectRequestState.value?.error}")
        }
    }

    LaunchedEffect(userDetailsState.value?.error) {
        if (userDetailsState.value?.error != null)
            userDetailsState.value?.error.let {
                context.showToast(it.toString())
            }
    }

    LaunchedEffect(true) {
        viewModel.reload()
    }

    Scaffold(bottomBar = {
        BottomNavBar(tabBarItems = bottomNavigationItems,
            navController = navController,
            selectedTabIndex = bottomNavigationItems.indexOf(BottomNavigationScreens.Profile),
            onAddItemClick = {
                navController.navigate(AddItemsDestination.route)
            })
    },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onLogOut,
                containerColor = primaryGreen
            ) {
                Text(text = "Logout")
            }
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(primaryGreen.copy(0.2f))
                    .padding(24.dp)
            ) {

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Profile", fontSize = 24.sp)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                            .size(70.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .crossfade(true)
                                .data(uiState.value.imageUrl)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                EditDetails(uiState)
            }

            Column(
                Modifier
                    .padding(16.dp)
            ) {
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    tabList.forEachIndexed { index, s ->
                        Tab(
                            selected = index == pagerState.currentPage,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(text = s, fontSize = 18.sp)
                        }
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.wrapContentSize()
                ) { index ->
                    when (index) {
                        0 -> {
                            Log.d("item", "FIrst: ${swapRequestList}")
                            SwapRequests(
                                myListedItemsState = swapRequestList,
                                onRequestAccept = { itemId, requestedWithItemId ->
                                    viewModel.acceptSwapRequest(
                                        itemId = itemId,
                                        swapWithItemId = requestedWithItemId
                                    )
                                },
                                onRequestReject = { itemId, requestedWithItemId ->
                                    viewModel.rejectSwapRequest(
                                        itemId = itemId,
                                        swapWithItemId = requestedWithItemId
                                    )
                                },
                                acceptRequestState = acceptRequestState,
                                rejectRequestState = rejectRequestState,
                                onSwapItemClick = onItemClick
                            )
                        }

                        1 -> {
                            MyListedItem(
                                myListedItemsState = myListedItemsState,
                                onItemClick = onItemClick,
                                onItemDelete = {
                                    viewModel.deleteItem(it)
                                }
                            )
                        }

                    }
                }
            }

        }
    }
}

@Composable
fun SwapRequests(
    myListedItemsState: SnapshotStateList<Pair<Item, Item>>,
    onRequestAccept: (String, String) -> Unit,
    onRequestReject: (String, String) -> Unit,
    acceptRequestState: State<RequestState?>,
    rejectRequestState: State<RequestState?>,
    onSwapItemClick: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (myListedItemsState.isEmpty()) {
            Text(text = "No swap requests")
        } else {
            myListedItemsState.forEach {


                SwapRequestCard(
                    item = it.first,
                    swapRequestItem = it.second,
                    onAccept = {
                        onRequestAccept(it.first.id, it.second.id)
                    },
                    onReject = {
                        onRequestReject(it.first.id, it.second.id)
                    },
                    isLoading = acceptRequestState.value?.isLoading == true || rejectRequestState.value?.isLoading == true,
                    onSwapItemClick = { onSwapItemClick(it.second.id) }
                )
            }
        }
    }
}

@Composable
fun SwapRequestCard(
    item: Item,
    swapRequestItem: Item,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    isLoading: Boolean,
    onSwapItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {


            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }
            Column(Modifier.fillMaxWidth()) {

                ItemInfo(item = item, title = "Your Item")
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .weight(1f)
                            .background(Color.Gray.copy(0.5f))
                    )
                    Icon(
                        imageVector = Icons.Default.SwapVerticalCircle,
                        contentDescription = "",
                        tint = primaryGreen,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(40.dp)

                    )
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .weight(1f)
                            .background(Color.Gray.copy(0.5f))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                ItemInfo(
                    item = swapRequestItem,
                    title = "Requester Item",
                    onSwapItemClick = onSwapItemClick
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(onClick = onAccept, modifier = Modifier.weight(1f)) {
                        Text(text = "Accept")
                    }
                    OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f)) {
                        Text(text = "Reject", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemInfo(item: Item, title: String, onSwapItemClick: () -> Unit = {}) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }

        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onSwapItemClick() }
                .height(250.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = item.description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(primaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.category, color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            fontSize = 14.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(primaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.condition, color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            fontSize = 14.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(primaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formattedDate(item.dateListed),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .width(150.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .crossfade(true)
                        .data(item.image[0])
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun MyListedItem(
    myListedItemsState: State<MyListedItemsState?>,
    onItemClick: (String) -> Unit,
    onItemDelete: (String) -> Unit
) {
    val myListedItems = myListedItemsState.value?.isSuccess
    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (myListedItemsState.value?.isLoading == true) {
            CircularProgressIndicator()
        } else {
            if (myListedItems == null) {

                Column(Modifier.fillMaxWidth()) {
                    Text(text = "No items listed")
                }

            } else {
                myListedItems.forEach { item ->
                    GridItem(
                        item = item.toItem(),
                        onClick = { onItemClick(item.key!!) },
                        onLike = {
                            onItemDelete(item.key!!)
                        },
                        iconRes = Icons.Outlined.DeleteOutline
                    )
                }
            }
        }
    }
}
//
//@Composable
//fun SwapHistory(
//    myListedItemsState: State<MyListedItemsState?>,
//    onItemClick: (String) -> Unit
//) {
//    val myListedItems = myListedItemsState.value?.isSuccess?.filter {
//        it.item?.itemSwapStatus != "Pending"
//    }?.map {
//        it.toItem()
//    }
//
//    Column(
//        Modifier
//            .fillMaxSize()
//            .padding(vertical = 16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        if (myListedItemsState.value?.isLoading == true) {
//            CircularProgressIndicator()
//        } else {
//            if (myListedItems == null) {
//
//                Column(Modifier.fillMaxWidth()) {
//                    Text(text = "No items swapped")
//                }
//
//            } else {
//                myListedItems.forEach { item ->
//                    SwapHistoryItem(
//                        item = item,
//                        onClick = { onItemClick(item.id) }
//                    )
//                }
//            }
//        }
//    }
//}


@Composable
fun SwapHistoryItem(item: Item, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }) {
        Box(
            Modifier
                .clip(RoundedCornerShape(24.dp))
                .fillMaxWidth()
                .height(200.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).crossfade(true)
                    .data(item.image[0])
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (item.itemSwapStatus == "Swapped") primaryGreen else Color.Red.copy(
                                0.5f
                            )
                        )
                ) {
                    Text(
                        text = item.itemSwapStatus,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
        Column(Modifier.padding(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Swap",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen
                )
                Icon(
                    imageVector = Icons.Outlined.SwapHoriz,
                    contentDescription = "",
                    tint = primaryGreen
                )
                Text(
                    text = item.category, fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen
                )
            }
            Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = item.category, fontSize = 13.sp, color = Color.Gray)
        }

    }

}

@Composable
fun EditDetails(
    uiState: State<ProfileUiState>
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Text(text = "Name", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(46.dp))
            Text(
                text = "${uiState.value.name}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryGreen
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Text(text = "Email", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(50.dp))
            Text(
                text = "${uiState.value.email}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryGreen
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Text(text = "Phone", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(42.dp))
            Text(
                text = "${uiState.value.phone}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryGreen
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Text(text = "Location", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "${uiState.value.location}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryGreen
            )
        }
    }
}
