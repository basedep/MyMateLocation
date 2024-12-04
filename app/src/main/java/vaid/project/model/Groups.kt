package vaid.project.model

data class Groups(
    val userID: String,
    val groupUsers: List<String>?,
    val groupName: String
)
