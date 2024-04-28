package uk.ac.tees.mad.d3574618.data.domain

import uk.ac.tees.mad.d3574618.data.database.FavoriteEntity
import java.util.Date

data class Item(
    val id: String = "",
    val name: String,
    val description: String = "",
    val keywords: String = "",
    val category: String,
    val condition: String,
    val listedBy: UserResponse.CurrentUser? = null,
    val swapRequests: List<FirestoreItemResponse.FirestoreItem>? = null,
    val image: List<String>,
    val dateListed: Date? = null,
    val itemSwapStatus: String = ""
)

fun Item.toFavoriteEntity() = FavoriteEntity(
    itemId = id
)
