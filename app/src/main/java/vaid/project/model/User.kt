package vaid.project.model

import java.io.Serializable

data class User(
    val id: String? = null,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val groupsIDs: List<String>?
): Serializable
