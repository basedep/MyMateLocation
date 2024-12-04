package vaid.project.database.remote

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage
import vaid.project.utils.Constants.BASE_URL
import vaid.project.utils.Constants.PROJECT_ID

class Appwrite private constructor() {

    companion object {
        @Volatile
        private var instance: Appwrite? = null

        fun getInstance(): Appwrite {
            return instance ?: synchronized(this) {
                instance ?: Appwrite().also { instance = it }
            }
        }
    }

    private var client: Client? = null
    private var database: Databases? = null
    private var account: Account? = null
    private var storage: Storage? = null

    fun initialize(context: Context) {
        if (client == null) {
            client = Client(context)
                .setEndpoint(BASE_URL)
                .setProject(PROJECT_ID)

            database = Databases(client!!)
            account = Account(client!!)
            storage = Storage(client!!)
        }
    }

    fun getDatabaseInstance(): Databases {
        return database ?: throw IllegalStateException("Database not initialized")
    }

    fun getAccountInstance(): Account {
        return account ?: throw IllegalStateException("Account not initialized")
    }
}
