package uk.ac.tees.mad.d3574618.data.domain

import java.util.Date

data class FirestoreItemResponse(
    val item: FirestoreItem?,
    val key: String?
) {
    data class FirestoreItem(
        val id: String,
        val name: String,
        val description: String,
        val image: List<String>,
        val dateListed: Date?,
        val keywords: String,
        val category: ItemCategory,
        val condition: ItemCondition,
        val swapRequests: List<FirestoreItem>? = null,
        val listedBy: UserResponse.CurrentUser? = null,
        val swapStatus: String? = null,
        val itemSwapStatus: String
    )
}