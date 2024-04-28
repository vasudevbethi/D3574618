package uk.ac.tees.mad.d3574618.data.domain


data class UserResponse(
    val item: CurrentUser?,
    val key: String?
) {
    data class CurrentUser(
        val name: String = "",
        val email: String = "",
        val phone: String = "",
        val profileImage: String = "",
        val listedItems: List<String> = emptyList(),
        val location: String = ""
    )
}