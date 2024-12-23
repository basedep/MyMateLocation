package vaid.project.model

import java.util.Date

data class Message(
    val id: String,
    val author: String,
    val messageText: String,
    val timestamp: Date
)