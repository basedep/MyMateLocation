package vaid.project.repository

import io.appwrite.Query
import io.appwrite.models.Document
import io.appwrite.models.Session
import vaid.project.database.remote.Appwrite
import vaid.project.database.remote.AppwriteAPI
import vaid.project.model.Groups
import vaid.project.model.User
import vaid.project.utils.Constants.COLLECTION_GROUPS
import vaid.project.utils.Constants.COLLECTION_USERS
import vaid.project.utils.Constants.DATABASE_ID

class Repository : AppwriteAPI {

    private val appwriteDatabase = Appwrite.getInstance().getDatabaseInstance()
    private val appwriteAccount = Appwrite.getInstance().getAccountInstance()

    override suspend fun login(email: String, password: String): Session =
          appwriteAccount.createEmailSession(email, password)

    override suspend fun signup(userID: String, name: String, email: String, password: String){
        appwriteAccount.create(userID, email, password, name)
    }

    override suspend fun addUser(user: User, userID: String) {
        appwriteDatabase.createDocument(DATABASE_ID, COLLECTION_USERS, userID, user)
    }

    override suspend fun updateUserLocation(userID: String, user: User) {
        appwriteDatabase.updateDocument(DATABASE_ID, COLLECTION_USERS, userID, user)
    }

    override suspend fun addGroup(group: Groups, id: String) {
        appwriteDatabase.createDocument(DATABASE_ID, COLLECTION_GROUPS, id, group)
    }


    override suspend fun getUser(): Map<String, String> {
        val credentials = appwriteAccount.get()
        return mapOf(
            "email" to credentials.email,
            "name" to credentials.name
         )
    }

    override suspend fun deleteSession(sessionId: String) {
        appwriteAccount.deleteSession(sessionId)
    }

    override suspend fun getAllUsers(): List<Document<Map<String, Any>>> {
        return appwriteDatabase.listDocuments(DATABASE_ID, COLLECTION_USERS).documents
    }

    override suspend fun addUserToGroup(groupId: String, newGroupDocument: Groups) {
        appwriteDatabase.updateDocument(DATABASE_ID, COLLECTION_GROUPS, groupId, newGroupDocument)
    }

    override suspend fun getAllGroups(userId: String): List<Document<Map<String, Any>>> {
        return appwriteDatabase.listDocuments(DATABASE_ID, COLLECTION_GROUPS,
            queries = listOf(Query.search("userID", userId))).documents
    }

    override suspend fun getAllGroupsUsers(ids: List<String>): List<Document<Map<String, Any>>> {
        return appwriteDatabase.listDocuments(DATABASE_ID, COLLECTION_USERS,
            queries = listOf(Query.equal( "id", ids))).documents
    }

    override suspend fun getGroupByNameAndUserId(groupName: String, userId: String): List<Document<Map<String, Any>>> {
        return appwriteDatabase.listDocuments(DATABASE_ID, COLLECTION_GROUPS,
            queries = listOf(Query.search("groupName", groupName), Query.search("userID", userId))).documents
    }


}