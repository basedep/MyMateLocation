package vaid.project.viewmodels

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.appwrite.models.Document
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import vaid.project.location.DefaultLocationClient
import vaid.project.model.Groups
import vaid.project.model.ParentItem
import vaid.project.model.User
import vaid.project.repository.Repository
import vaid.project.utils.Result
import java.util.UUID

class LocationViewModel(
    private val locationClient: DefaultLocationClient,
    private val repository: Repository
) : ViewModel() {

    var location: MutableLiveData<Result<Location>> = MutableLiveData<Result<Location>>()
    var users: MutableLiveData<List<User>> = MutableLiveData<List<User>>()
    var parentItems: MutableLiveData<List<ParentItem>> = MutableLiveData<List<ParentItem>>()

    init {
        getCurrentLocation()
    }

    private fun getCurrentLocation() = viewModelScope.launch {
        locationClient.getLocationUpdates(5000)
            .collect { result ->
                location.value = result
            }
    }


    fun signup(name: String, email: String, password: String) = viewModelScope.launch {
        try {
            val id = UUID.randomUUID().toString()
            val groupId = UUID.randomUUID().toString()
            val user = User(id, name, 0.0, 0.0, listOf(groupId))
            val group = Groups(groupId, id, null, "Users")
            repository.signup(id, name, email, password)
            repository.addUser(user, id)
            repository.addGroup(group, groupId)
        } catch (e: Exception) {
            Log.d("vaid", "signup: $e")
            this.cancel()
        }
    }

    fun updateUserLocation(userId: String, user: User) = viewModelScope.launch{
        repository.updateUserLocation(userId, user)
    }


    fun login(email: String, password: String) = viewModelScope.async {
        repository.login(email, password)
    }

    fun getUser() = viewModelScope.async {
        repository.getUser()
    }

    fun deleteSession(sessionId: String) = viewModelScope.launch{
        repository.deleteSession(sessionId)
    }

    fun getAllUsers(userId: String) = viewModelScope.launch{
        val allUsers = extractData(repository.getAllUsers()).filterNot {
            it.id == userId
        }
        users.postValue(allUsers)
    }

    fun addGroup(groupId: String, group: Groups) = viewModelScope.launch{
        repository.addGroup(group, groupId)
    }

    fun addUserToGroup(groupId: String, newGroupDocument: Groups) = viewModelScope.launch{
        repository.addUserToGroup(groupId, newGroupDocument)
    }

    fun getGroupByNameAndUserId(name: String, userId: String) = viewModelScope.async{
        val group = extractDataFromGroups(repository.getGroupByNameAndUserId(name, userId))
        group
    }

    fun getGroupUsers(groupName: String, userId: String) = viewModelScope.async{
        val group = extractDataFromGroups(repository.getGroupByNameAndUserId(groupName, userId))[0]
        val users = extractData(repository.getAllGroupsUsers(group.groupUsers!!))
        users
    }

    fun getAllGroups(userId: String) = viewModelScope.launch{
        val groups = extractDataFromGroups(repository.getAllGroups(userId))
        val items: MutableList<ParentItem> = mutableListOf()

        for (group in groups){
            val userIds = group.groupUsers?.map {
                it
            }
            print(userIds)
            val groupUsers =
                if (!userIds.isNullOrEmpty())
                     extractData(repository.getAllGroupsUsers(userIds))
                else
                    listOf()

            val groupName = group.groupName
            items.add(ParentItem(groupName, groupUsers))
        }

        parentItems.postValue(items)
    }



    private fun extractData(documents: List<Document<Map<String, Any>>>): List<User> {
        val gson = Gson()
        val dataList = mutableListOf<User>()

        for (document in documents) {
            val dataMap: Map<String, Any> = document.toMap()
            val json = gson.toJson(dataMap["data"])

            val data: User = gson.fromJson(json, User::class.java)

            dataList.add(data)
        }

        return dataList
    }


    private fun extractDataFromGroups(documents: List<Document<Map<String, Any>>>): List<Groups> {
        val gson = Gson()
        val dataList = mutableListOf<Groups>()

        for (document in documents) {
            val dataMap: Map<String, Any> = document.toMap()
            val json = gson.toJson(dataMap["data"])

            val data: Groups = gson.fromJson(json, Groups::class.java)

            dataList.add(data)
        }

        return dataList
    }
}