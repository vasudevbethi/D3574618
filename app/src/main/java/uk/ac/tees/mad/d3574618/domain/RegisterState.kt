package uk.ac.tees.mad.d3574618.domain

data class RegisterState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)