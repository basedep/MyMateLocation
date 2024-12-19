package vaid.project.model

data class User(
    val id: String? = null,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val groupsIDs: List<String>?
)
