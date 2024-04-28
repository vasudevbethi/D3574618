package uk.ac.tees.mad.d3574618.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import uk.ac.tees.mad.d3574618.data.domain.FirestoreItemResponse
import uk.ac.tees.mad.d3574618.data.domain.Item
import uk.ac.tees.mad.d3574618.data.domain.toItem


@Composable
fun SwapPopupBox(
    popupWidth: Float = 350f,
    showPopup: Boolean,
    onClickOutside: () -> Unit,
    onConfirm: (String) -> Unit,
    items: List<FirestoreItemResponse>?,
    isLoading: Boolean
) {
    val selectedItemIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    if (showPopup) {
        // full screen background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(0.5f))
                .zIndex(10F),
            contentAlignment = Alignment.Center
        ) {
            // popup
            Popup(alignment = Alignment.Center, properties = PopupProperties(
                excludeFromSystemGesture = true,
            ),
                // to dismiss on click outside
                onDismissRequest = { onClickOutside() }) {
                if (isLoading) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .width(popupWidth.dp)
                            .height(popupWidth.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .width(popupWidth.dp)
                            .background(Color.White), contentAlignment = Alignment.Center
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = "Select the item you want to swap with:", fontSize = 18.sp)
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(vertical = 20.dp)
                                    .verticalScroll(
                                        rememberScrollState()
                                    ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items?.forEachIndexed { index, item ->
                                    ListedItem(
                                        item.toItem(),
                                        onItemSelected = {
                                            selectedItemIndex.intValue = index
                                        },
                                        isSelected = selectedItemIndex.intValue == index
                                    )
                                }
                            }
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onConfirm(items?.get(selectedItemIndex.intValue)?.key!!) }) {
                                Text(text = "Confirm")
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun ListedItem(item: Item, onItemSelected: () -> Unit, isSelected: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()

            .clip(RoundedCornerShape(16.dp))
            .clickable { onItemSelected() }
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier
                .padding(8.dp)
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 12.dp)
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
            Column {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else Color.Unspecified
                )
                Text(
                    text = item.category,
                    color = if (isSelected) Color.White else Color.Unspecified
                )
                Text(
                    text = item.description,
                    maxLines = 2,
                    color = if (isSelected) Color.White else Color.Unspecified
                )
            }
        }
    }
}


