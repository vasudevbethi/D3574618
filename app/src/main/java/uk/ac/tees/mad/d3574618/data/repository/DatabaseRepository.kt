package uk.ac.tees.mad.d3574618.data.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.d3574618.data.database.FavoriteEntity
import uk.ac.tees.mad.d3574618.data.database.ItemDao
import uk.ac.tees.mad.d3574618.data.domain.Item
import uk.ac.tees.mad.d3574618.data.domain.toFavoriteEntity

interface DatabaseRepository {

    suspend fun addToFavorite(item: Item)

    fun getAllFavorite(): Flow<List<FavoriteEntity>>

    suspend fun deleteFromFavorite(item: Item)
}

class DatabaseRepositoryImpl(
    private val dao: ItemDao
) : DatabaseRepository {
    override suspend fun addToFavorite(item: Item) {
        dao.addToFavorite(item.toFavoriteEntity())
    }

    override fun getAllFavorite(): Flow<List<FavoriteEntity>> = dao.getAllFavorite()

    override suspend fun deleteFromFavorite(item: Item) =
        dao.deleteFromFavorite(item.toFavoriteEntity())

}