package uk.ac.tees.mad.d3574618.data.domain

data class LoginState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)

data class LoginStatus(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)

data class CurrentUser(
    val isLoading: Boolean = false,
    val isSuccess: UserResponse? = null,
    val isError: String? = null
)

data class UserData(
    val userId: String,
    val username: String?,
    val email: String?
)
data class SignInResult(
    val data: UserData? = null,
    val isSuccessful: Boolean = false,
    val errorMessage: String? = null
)