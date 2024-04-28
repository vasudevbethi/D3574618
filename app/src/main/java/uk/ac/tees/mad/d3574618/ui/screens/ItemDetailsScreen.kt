package uk.ac.tees.mad.d3574618.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.data.domain.Item
import uk.ac.tees.mad.d3574618.data.domain.toItem
import uk.ac.tees.mad.d3574618.dial
import uk.ac.tees.mad.d3574618.formattedDate
import uk.ac.tees.mad.d3574618.sendMail
import uk.ac.tees.mad.d3574618.showToast
import uk.ac.tees.mad.d3574618.ui.components.ExpandableText
import uk.ac.tees.mad.d3574618.ui.components.SwapPopupBox
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination
import uk.ac.tees.mad.d3574618.ui.viewmodels.ItemDetailsViewModel


object ItemDetailsDestination : NavigationDestination {
    override val route: String = "item_detail"
    override val titleRes: Int = R.string.item_details
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemDetailsScreen(
    onBack: () -> Unit,
    viewModel: ItemDetailsViewModel = hiltViewModel()
) {
    val itemState = viewModel.itemDetail.collectAsState(initial = null)
    val myListedItemsState = viewModel.myListedItemsState.collectAsState(initial = null)
    val swapRequestState = viewModel.swapRequestState.collectAsState(initial = null)
    val item = itemState.value?.isSuccess?.toItem()

    val context = LocalContext.current
    val pagerState = rememberPagerState {
        item?.image?.size ?: 0
    }
    var showPopup by rememberSaveable { mutableStateOf(false) }
    val showPopupLoading by rememberSaveable { mutableStateOf(false) }

//    LaunchedEffect(true) {
//        viewModel.reload()
//    }

    LaunchedEffect(swapRequestState.value?.data) {
        if (!swapRequestState.value?.data.isNullOrEmpty()) {
            showPopup = false
            context.showToast("Swap Request sent")
        }
    }
    LaunchedEffect(swapRequestState.value?.error) {
        if (!swapRequestState.value?.error.isNullOrEmpty()) {
            context.showToast(swapRequestState.value?.error.toString())
        }
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        showPopup = true
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
//                    if (addItemState.value?.isLoading == true) {
//                        CircularProgressIndicator(color = Color.White)
//                    } else {
                Text(text = "Swap", fontSize = 20.sp, color = Color.White)
//                    }
            }
        }
    ) {

        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (itemState.value?.isLoading == true) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {

                Column(Modifier.fillMaxSize()) {

                    Column(
                        Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HeaderContent(onBack = onBack, pagerState = pagerState, item = item)
                        PageIndicator(
                            pageCount = item?.image?.size ?: 0,
                            currentPage = pagerState.currentPage,
                            modifier = Modifier.padding(vertical = 4.dp)

                        )
                    }
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                                Text(
                                    text = "${item?.name}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = "${item?.category}",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(text = "|")
                                    Text(
                                        text = "${item?.swapRequests?.size} swap requests"
                                    )
                                }
                                Text(
                                    text = "Listed on: ${formattedDate(item?.dateListed)}"
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(16.dp)
                                    )
                                    .background(MaterialTheme.colorScheme.primary)


                            ) {
                                Text(
                                    text = "${item?.condition}",
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                                )
                            }
                        }
                        Row(Modifier.padding(vertical = 16.dp)) {
                            ExpandableText(
                                text = "${item?.description}",
                                style = TextStyle(
                                    color = Color.Gray.copy(alpha = 0.9f),
                                    fontSize = 18.sp
                                )
                            )
                        }
                        Column(Modifier.padding(vertical = 16.dp)) {
                            Text(
                                text = "Listed by: ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .border(
                                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                        RoundedCornerShape(16.dp)
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier

                                        .padding(12.dp)
                                        .size(90.dp)
                                        .clip(CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .crossfade(true)
                                            .data(item?.listedBy?.profileImage)
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxWidth(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${item?.listedBy?.name}",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(text = "${item?.listedBy?.email}", fontSize = 16.sp)
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = "Email",
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clickable {
                                                    context.sendMail(
                                                        to = item?.listedBy?.email!!,
                                                        subject = "Regarding ${item.name} listed on Reusable item exchange"
                                                    )
                                                },
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Message,
                                            modifier = Modifier.size(40.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                            contentDescription = "Message",
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clickable {
                                                    context.dial(item?.listedBy?.phone!!)
                                                },
                                            tint = MaterialTheme.colorScheme.primary,
                                            contentDescription = "Call"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        SwapPopupBox(
            showPopup = showPopup,
            onClickOutside = { showPopup = false },
            onConfirm = { swapItemId ->
                viewModel.requestForSwap(swapItemId)
            },
            items = myListedItemsState.value?.isSuccess,
            isLoading = ((showPopupLoading) || (myListedItemsState.value?.isLoading == true) || (swapRequestState.value?.isLoading == true))
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeaderContent(
    onBack: () -> Unit,
    pagerState: PagerState,
    item: Item?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
    ) {
        IconButton(
            onClick = onBack, modifier = Modifier
                .padding(16.dp)
                .zIndex(100f)
                .clip(CircleShape)
                .background(
                    Color.Gray.copy(0.5f)
                )
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Back",
                modifier = Modifier.size(30.dp)
            )
        }
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState
        ) { currentPage ->
            if (item != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).crossfade(true)
                        .data(item.image[currentPage])
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }

}

@Composable
fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(pageCount) {
            IndicatorDot(isSelected = it == currentPage)
        }
    }
}

@Composable
fun IndicatorDot(
    isSelected: Boolean
) {
    val width =
        animateDpAsState(targetValue = if (isSelected) 10.dp else 8.dp, label = "Indicator width")
    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(width.value)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.5f
                )
            )
    )

}