package uk.ac.tees.mad.d3574618.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorite(favoriteEntity: FavoriteEntity)

    @Query("select * from FavoriteEntity")
    fun getAllFavorite(): Flow<List<FavoriteEntity>>

    @Delete
    suspend fun deleteFromFavorite(favoriteEntity: FavoriteEntity)

}