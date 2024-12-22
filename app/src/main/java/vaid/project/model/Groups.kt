package vaid.project.model

data class Groups(
    val id: String,
    val userID: String,
    val groupUsers: MutableList<String>?,
    val groupName: String
)
