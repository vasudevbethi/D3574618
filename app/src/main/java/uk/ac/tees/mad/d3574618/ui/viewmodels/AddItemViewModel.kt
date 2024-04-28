package uk.ac.tees.mad.d3574618.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.data.domain.AddItemUiState
import uk.ac.tees.mad.d3574618.data.domain.Resource
import uk.ac.tees.mad.d3574618.data.repository.FirestoreRepository
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddItemUiState())
    val uiState = _uiState.asStateFlow()

    private val _itemAddState = Channel<ItemAddState>()
    val itemAddState = _itemAddState.receiveAsFlow()

    fun updateUiState(newState: AddItemUiState) {
        _uiState.value = newState
    }

    fun resetUiState() {
        _uiState.value = AddItemUiState()
    }


    fun addItem() = viewModelScope.launch {
        val currentDateTime = Date()

        updateUiState(
            _uiState.value.copy(
                listedDate = currentDateTime,
                listedByKey = firebaseAuth.currentUser?.uid!!
            )
        )

        firestoreRepository.submitItem(_uiState.value).collect {
            when (it) {
                is Resource.Error -> {
                    _itemAddState.send(
                        ItemAddState(
                            error = it.message
                        )
                    )
                }

                is Resource.Loading -> {
                    _itemAddState.send(
                        ItemAddState(
                            isLoading = true
                        )
                    )
                }

                is Resource.Success -> {
                    _itemAddState.send(
                        ItemAddState(
                            data = it.data
                        )
                    )
                }
            }
        }
    }
}

data class ItemAddState(
    val data: String? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)