package uk.ac.tees.mad.d3574618.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.data.domain.FirestoreItemResponse
import uk.ac.tees.mad.d3574618.data.domain.Resource
import uk.ac.tees.mad.d3574618.data.repository.FirestoreRepository
import uk.ac.tees.mad.d3574618.ui.screens.ItemDetailsDestination
import javax.inject.Inject


@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val itemId: String = checkNotNull(savedStateHandle[ItemDetailsDestination.itemIdArg])

    private val _itemDetail = Channel<RetrievedItemState>()
    val itemDetail = _itemDetail.receiveAsFlow()

    private val _swapRequestState = Channel<ItemAddState>()
    val swapRequestState = _swapRequestState.receiveAsFlow()

    private val _myListedItemsState = Channel<MyListedItemsState>()
    val myListedItemsState = _myListedItemsState.receiveAsFlow()

    init {
        reload()
    }

    fun reload() {
        getItemByKey(itemId)
        getMyListedItems()
    }

    private fun getItemByKey(key: String) = viewModelScope.launch {
        firestoreRepository.getItemById(key).collect {
            when (it) {
                is Resource.Error -> {
                    _itemDetail.send(RetrievedItemState(isError = it.message))
                }

                is Resource.Loading -> {
                    _itemDetail.send(RetrievedItemState(isLoading = true))
                }

                is Resource.Success -> {
                    _itemDetail.send(RetrievedItemState(isSuccess = it.data))
                }
            }
        }
    }

    private fun getMyListedItems() = viewModelScope.launch {
        firestoreRepository.getMyListedItems().collect {
            when (it) {
                is Resource.Error -> {
                    _myListedItemsState.send(MyListedItemsState(isError = it.message))
                }

                is Resource.Loading -> {
                    _myListedItemsState.send(MyListedItemsState(isLoading = true))
                }

                is Resource.Success -> {
                    _myListedItemsState.send(MyListedItemsState(isSuccess = it.data))
                }
            }
        }
    }

    fun requestForSwap(swapWithItemId: String) = viewModelScope.launch {
        firestoreRepository.requestForSwap(itemId = itemId, swapWithItemId = swapWithItemId)
            .collect {
                when (it) {
                    is Resource.Error -> {
                        _swapRequestState.send(ItemAddState(error = it.message))
                    }

                    is Resource.Loading -> {
                        _swapRequestState.send(ItemAddState(isLoading = true))
                    }

                    is Resource.Success -> {
                        _swapRequestState.send(ItemAddState(data = it.data))
                    }
                }
            }
    }
}

data class RetrievedItemState(
    val isLoading: Boolean = false,
    val isSuccess: FirestoreItemResponse? = null,
    val isError: String? = null
)