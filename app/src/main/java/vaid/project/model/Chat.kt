package vaid.project.model

data class Chat(
    val chatId: String,
    val firstUserId: String,
    val secondUserId: String,
    val messagesIds: MutableList<String>?
)