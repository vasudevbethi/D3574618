package uk.ac.tees.mad.d3574618.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import uk.ac.tees.mad.d3574618.data.domain.Item
import uk.ac.tees.mad.d3574618.ui.theme.primaryGreen


@Composable
fun GridItem(
    item: Item,
    onClick: () -> Unit,
    onLike: () -> Unit,
    iconRes: ImageVector = Icons.Outlined.FavoriteBorder
) {
    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(24.dp))
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
                    onClick = onLike,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))

                ) {
                    Icon(imageVector = iconRes, contentDescription = null)
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