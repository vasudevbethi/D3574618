package uk.ac.tees.mad.d3574618.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.data.domain.FirestoreItemResponse
import uk.ac.tees.mad.d3574618.data.domain.Item
import uk.ac.tees.mad.d3574618.data.domain.Resource
import uk.ac.tees.mad.d3574618.data.repository.FirestoreRepository
import uk.ac.tees.mad.d3574618.data.repository.DatabaseRepository
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val databaseRepo: DatabaseRepository,
) : ViewModel() {

    private val _itemsList = Channel<FirestoreRetrievedItemState>()
    val itemsList = _itemsList.receiveAsFlow()

    init {
        getItemList()
    }

    fun getItemList() = viewModelScope.launch {
        firestoreRepository.getAllItems().collect {
            when (it) {
                is Resource.Error -> {
                    _itemsList.send(FirestoreRetrievedItemState(isError = it.message))
                }

                is Resource.Loading -> {
                    _itemsList.send(FirestoreRetrievedItemState(isLoading = true))

                }

                is Resource.Success -> {
                    _itemsList.send(FirestoreRetrievedItemState(isSuccess = it.data))
                }
            }
        }
    }

    fun addItemToFavorite(item: Item) = viewModelScope.launch {
        databaseRepo.addToFavorite(item)
    }.invokeOnCompletion {

    }
}

data class FirestoreRetrievedItemState(
    val isLoading: Boolean = false,
    val isSuccess: List<FirestoreItemResponse>? = null,
    val isError: String? = null
)