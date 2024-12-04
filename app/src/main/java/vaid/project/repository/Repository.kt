package vaid.project.repository

import android.util.Log
import io.appwrite.models.Session
import vaid.project.database.remote.Appwrite
import vaid.project.database.remote.AppwriteAPI
import vaid.project.location.LocationClient
import vaid.project.model.Groups
import vaid.project.model.User
import vaid.project.utils.Constants.COLLECTION_GROUPS
import vaid.project.utils.Constants.COLLECTION_USERS
import vaid.project.utils.Constants.DATABASE_ID
import vaid.project.utils.Result
import java.util.UUID

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

}