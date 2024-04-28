package uk.ac.tees.mad.d3574618.data.domain

import java.util.Date

data class AddItemUiState(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val keywords: String = "",
    val category: String = "",
    val condition: String = "",
    val listedByKey: String = "",
    val listedDate: Date = Date(),
    val images: List<ByteArray> = emptyList()
)
