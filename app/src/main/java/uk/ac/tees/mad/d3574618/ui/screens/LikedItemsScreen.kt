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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import uk.ac.tees.mad.d3574618.R
import uk.ac.tees.mad.d3574618.data.domain.Item
import uk.ac.tees.mad.d3574618.data.domain.toItem
import uk.ac.tees.mad.d3574618.showToast
import uk.ac.tees.mad.d3574618.ui.navigation.NavigationDestination
import uk.ac.tees.mad.d3574618.ui.viewmodels.LikedItemViewModel

@Composable
fun LikedItemsScreen(
    onNavigateUp: () -> Unit,
    onItemClick: (String) -> Unit,
    viewModel: LikedItemViewModel = hiltViewModel()
) {
    val likedItemsListState = viewModel.likedItemListState.collectAsState(initial = null)
    val context = LocalContext.current

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Clear,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onNavigateUp()
                    })

            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Liked Items", fontSize = 24.sp, fontWeight = FontWeight.Medium
                )
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (likedItemsListState.value?.isLoading == true) {
                CircularProgressIndicator()
            } else {
                if (likedItemsListState.value?.isSuccess.isNullOrEmpty()) {
                    Text(text = "No liked items")
                } else {
                    likedItemsListState.value?.isSuccess?.forEach {
                        FavoriteItem(
                            item = it.toItem(),
                            onClick = { onItemClick(it.key!!) },
                            onUnLike = {
                                viewModel.deleteFromFavorite(it.toItem())
                                context.showToast("Remove from favorite")
                            }
                        )
                    }
                }
            }
        }
    }
}

object LikedItemsDestination : NavigationDestination {
    override val route: String
        get() = "liked_items"
    override val titleRes: Int
        get() = R.string.liked_items

}


@Composable
fun FavoriteItem(item: Item, onClick: () -> Unit, onUnLike: () -> Unit) {
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
                IconButton(
                    onClick = onUnLike,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = 0.9f))

                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = Color.White
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