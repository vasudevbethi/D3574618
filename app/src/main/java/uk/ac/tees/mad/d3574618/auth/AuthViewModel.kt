package uk.ac.tees.mad.d3574618.auth

import android.location.Location
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3574618.data.domain.CurrentUser
import uk.ac.tees.mad.d3574618.data.domain.LoginState
import uk.ac.tees.mad.d3574618.data.domain.LoginStatus
import uk.ac.tees.mad.d3574618.data.domain.RegisterState
import uk.ac.tees.mad.d3574618.data.domain.Resource
import uk.ac.tees.mad.d3574618.data.domain.SignInResult
import uk.ac.tees.mad.d3574618.data.domain.UserData
import uk.ac.tees.mad.d3574618.data.repository.FirestoreRepository
import uk.ac.tees.mad.d3574618.data.repository.ReusableItemExchangeRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: ReusableItemExchangeRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _signInStatus = Channel<LoginStatus>()
    val signInState = _signInStatus.receiveAsFlow()

    private val _updateDetailStatus = Channel<LoginStatus>()
    val updateDetailsStatus = _updateDetailStatus.receiveAsFlow()

    private val _currentUserStatus = Channel<CurrentUser>()
    val currentUserStatus = _currentUserStatus.receiveAsFlow()

    private val _googleSignInResult = MutableStateFlow(SignInResult())
    val googleSignInResult = _googleSignInResult.asStateFlow()

    private val _signupUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState = _signupUiState.asStateFlow()

    private val _moreDetailsUiState = MutableStateFlow(MoreDetailsUiState())
    val moreDetailsUiState = _moreDetailsUiState.asStateFlow()

    private val _signUpState = Channel<RegisterState>()
    val signUpState = _signUpState.receiveAsFlow()

    fun resetState() {
        _state.update { LoginState() }
        _googleSignInResult.update { SignInResult() }
    }

    fun updateLoginState(value: LoginUiState) {
        _loginUiState.value = value
    }

    fun updateMoreDetailState(value: MoreDetailsUiState) {
        _moreDetailsUiState.value = value
    }

    fun onSignInWithGoogleResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null, signInError = result.errorMessage
            )
        }
    }

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        repository.loginUser(email, password).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _signInStatus.send(LoginStatus(isError = result.message))
                }

                is Resource.Loading -> {
                    _signInStatus.send(LoginStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _signInStatus.send(LoginStatus(isSuccess = "Sign In Success"))

                }
            }
        }
    }

    fun forgotPassword(email: String) = viewModelScope.launch {
        repository.forgotPassword(email).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _signInStatus.send(LoginStatus(isError = result.message))
                }

                is Resource.Loading -> {
                    _signInStatus.send(LoginStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _signInStatus.send(LoginStatus(isSuccess = "Sent forget password email."))

                }
            }
        }
    }

    fun saveUserInFirestore(user: UserData) = viewModelScope.launch {
        repository.saveUser(
            email = user.email,
            username = user.username,
            userId = user.userId
        )
    }

    fun updateUser() = viewModelScope.launch {
        repository.updateCurrentUser(_moreDetailsUiState.value).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _updateDetailStatus.send(LoginStatus(isError = result.message))
                }

                is Resource.Loading -> {
                    _updateDetailStatus.send(LoginStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _updateDetailStatus.send(LoginStatus(isSuccess = "Updated successfully"))

                }
            }
        }
    }

    init {
        getUserDetails()
    }

    fun getUserDetails() =
        viewModelScope.launch {

            firestoreRepository.getCurrentUser().collect{ result ->
                when(result) {
                    is Resource.Error -> {
                        _currentUserStatus.send(CurrentUser(isError = result.message))
                    }

                    is Resource.Loading -> {
                        _currentUserStatus.send(CurrentUser(isLoading = true))
                    }

                    is Resource.Success -> {
                        _currentUserStatus.send(CurrentUser(isSuccess = result.data))

                    }
                }
            }
        }


    fun updateSignUpState(value: SignUpUiState) {
        _signupUiState.value = value
    }

    fun registerUser(email: String, password: String, username: String) =
        viewModelScope.launch {
            repository.registerUser(email, password, username).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _signUpState.send(RegisterState(isError = result.message))
                    }

                    is Resource.Loading -> {
                        _signUpState.send(RegisterState(isLoading = true))
                    }

                    is Resource.Success -> {
                        _signUpState.send(RegisterState(isSuccess = "Register Success"))
                    }
                }
            }
        }

    val currentLocation = MutableStateFlow<Location?>(null)




}

data class LoginUiState(
    val email: String = "", val password: String = ""
)

data class SignUpUiState(
    val name: String = "", val email: String = "", val password: String = ""
)

data class MoreDetailsUiState(
    val phone: String = "",
    val images: ByteArray? = null,
    val location: String = ""
)