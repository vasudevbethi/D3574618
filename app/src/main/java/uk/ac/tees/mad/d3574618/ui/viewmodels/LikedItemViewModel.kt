package uk.ac.tees.mad.d3574618.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.data.domain.Item
import uk.ac.tees.mad.d3574618.data.domain.Resource
import uk.ac.tees.mad.d3574618.data.repository.FirestoreRepository
import uk.ac.tees.mad.d3574618.data.repository.DatabaseRepository
import javax.inject.Inject

@HiltViewModel
class LikedItemViewModel @Inject constructor(
    private val databaseRepo: DatabaseRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    init {
        loadAllLikedItems()
    }

    private val _likedItemListState = Channel<FirestoreRetrievedItemState>()
    val likedItemListState = _likedItemListState.receiveAsFlow()

    var allFavorites by mutableStateOf(listOf<String>())
        private set

    fun loadAllLikedItems() {
        viewModelScope.launch {
            databaseRepo.getAllFavorite().onEach {
                allFavorites = it.map { favorite ->
                    favorite.itemId
                }
                getLikedItemList()
            }.launchIn(viewModelScope)
        }
    }

    private fun getLikedItemList() = viewModelScope.launch {
        firestoreRepository.getItemsByKeyList(allFavorites).collect {
            when (it) {
                is Resource.Error -> {
                    _likedItemListState.send(FirestoreRetrievedItemState(isError = it.message))
                }

                is Resource.Loading -> {
                    _likedItemListState.send(FirestoreRetrievedItemState(isLoading = true))

                }

                is Resource.Success -> {
                    _likedItemListState.send(FirestoreRetrievedItemState(isSuccess = it.data))
                }
            }
        }
    }

    fun deleteFromFavorite(item: Item) = viewModelScope.launch {
        databaseRepo.deleteFromFavorite(item)
    }.invokeOnCompletion {
        loadAllLikedItems()
    }
}