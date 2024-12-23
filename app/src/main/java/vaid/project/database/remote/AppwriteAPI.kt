package vaid.project.database.remote

import io.appwrite.models.Document
import io.appwrite.models.Session
import vaid.project.model.Chat
import vaid.project.model.Groups
import vaid.project.model.Message
import vaid.project.model.User

interface AppwriteAPI {

    suspend fun login(email:String, password: String): Session

    suspend fun signup(userID: String, name: String, email:String, password: String)

    suspend fun addUser(user: User, userID: String)

    suspend fun updateUserLocation(userID: String, user: User)

    suspend fun addGroup(group: Groups, id: String)

    suspend fun getUser(): Map<String, String>

    suspend fun deleteSession(sessionId: String)

    suspend fun getAllUsers(): List<Document<Map<String, Any>>>

    suspend fun addUserToGroup(groupId: String, newGroupDocument: Groups)

    suspend fun getAllGroups(userId: String): List<Document<Map<String, Any>>>

    suspend fun getAllGroupsUsers(ids: List<String>): List<Document<Map<String, Any>>>

    suspend fun getGroupByNameAndUserId(groupName: String, userId: String): List<Document<Map<String, Any>>>

    suspend fun getAllChatMessages(messageIds: List<String>): List<Document<Map<String, Any>>>

    suspend fun getChat(firstId: String, secondId: String): List<Document<Map<String, Any>>>

    suspend fun createChat(chatId: String, chat: Chat)

    suspend fun updateChatMessages(chat: Chat)

    suspend fun sendMessage(messageId: String, message: Message)

}