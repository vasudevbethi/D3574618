package uk.ac.tees.mad.d3574618.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.data.domain.FirestoreItemResponse
import uk.ac.tees.mad.d3574618.data.domain.Resource
import uk.ac.tees.mad.d3574618.data.domain.UserResponse
import uk.ac.tees.mad.d3574618.data.repository.FirestoreRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _myListedItemsState = Channel<MyListedItemsState>()
    val myListedItemsState = _myListedItemsState.receiveAsFlow()

    private val _currentUserData = Channel<UserState>()
    val currentUserData = _currentUserData.receiveAsFlow()

    private val _acceptSwapState = Channel<RequestState>()
    val acceptSwapState = _acceptSwapState.receiveAsFlow()

    private val _rejectSwapState = Channel<RequestState>()
    val rejectSwapState = _rejectSwapState.receiveAsFlow()

    private val _deleteItemState = Channel<RequestState>()
    val deleteItemState = _deleteItemState.receiveAsFlow()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        reload()
    }

    fun updateUiState(value: ProfileUiState) {
        _uiState.update {
            value
        }
    }

    fun reload() {
        getMyListedItems()
        getUserData()
    }

    fun getMyListedItems() = viewModelScope.launch {
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

    fun acceptSwapRequest(itemId: String, swapWithItemId: String) = viewModelScope.launch {
        firestoreRepository.acceptSwapRequest(itemId, swapWithItemId).collect {
            when (it) {
                is Resource.Error -> {
                    _acceptSwapState.send(RequestState(error = it.message))
                }

                is Resource.Loading -> {
                    _acceptSwapState.send(RequestState(isLoading = true))
                }

                is Resource.Success -> {
                    _acceptSwapState.send(RequestState(data = it.data))
                }
            }
        }
    }

    fun rejectSwapRequest(itemId: String, swapWithItemId: String) = viewModelScope.launch {

        firestoreRepository.rejectSwapRequest(itemId, swapWithItemId).collect {
            when (it) {
                is Resource.Error -> {
                    _rejectSwapState.send(RequestState(error = it.message))
                }

                is Resource.Loading -> {
                    _rejectSwapState.send(RequestState(isLoading = true))
                }

                is Resource.Success -> {
                    _rejectSwapState.send(RequestState(data = it.data))
                }
            }
        }
    }

    fun deleteItem(itemId: String) = viewModelScope.launch {

        firestoreRepository.deleteItem(itemId).collect {
            when (it) {
                is Resource.Error -> {
                    _deleteItemState.send(RequestState(error = it.message))
                }

                is Resource.Loading -> {
                    _deleteItemState.send(RequestState(isLoading = true))
                }

                is Resource.Success -> {
                    _deleteItemState.send(RequestState(data = it.data))
                }
            }
        }
    }


    private fun getUserData() = viewModelScope.launch {
        firestoreRepository.getCurrentUser().collect {
            when (it) {
                is Resource.Error -> {
                    _currentUserData.send(UserState(error = it.message))
                }

                is Resource.Loading -> {
                    _currentUserData.send(UserState(isLoading = true))
                }

                is Resource.Success -> {
                    _currentUserData.send(UserState(data = it.data))
                }
            }
        }
    }
}

data class RequestState(
    val data: String? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)

data class ProfileUiState(
    val name: String = "",
    val imageUrl: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = ""
)

data class MyListedItemsState(
    val isLoading: Boolean = false,
    val isSuccess: List<FirestoreItemResponse>? = null,
    val isError: String? = null
)

data class UserState(
    val data: UserResponse? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)