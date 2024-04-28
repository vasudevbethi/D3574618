package uk.ac.tees.mad.d3574618.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteEntity(
    @PrimaryKey
    val itemId: String
)