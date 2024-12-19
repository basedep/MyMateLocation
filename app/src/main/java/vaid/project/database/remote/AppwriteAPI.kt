package vaid.project.database.remote

import io.appwrite.models.Document
import io.appwrite.models.Session
import vaid.project.model.Groups
import vaid.project.model.User
import vaid.project.utils.Result

interface AppwriteAPI {

    suspend fun login(email:String, password: String): Session

    suspend fun signup(userID: String, name: String, email:String, password: String)

    suspend fun addUser(user: User, userID: String)

    suspend fun updateUserLocation(userID: String, user: User)

    suspend fun addGroup(group: Groups, id: String)

    suspend fun getUser(): Map<String, String>

    suspend fun deleteSession(sessionId: String)

    suspend fun getAllUsers(): List<Document<Map<String, Any>>>

    suspend fun addUserToGroup(groupId: String, userId: String)

    suspend fun getAllGroups(userId: String): List<Document<Map<String, Any>>>

    suspend fun getAllGroupsUsers(ids: List<String>): List<Document<Map<String, Any>>>
}