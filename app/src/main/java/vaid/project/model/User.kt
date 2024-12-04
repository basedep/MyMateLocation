package vaid.project.model

data class User(
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val groupsIDS: List<String>?
)
