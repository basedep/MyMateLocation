package vaid.project.viewmodels

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.appwrite.ID
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import vaid.project.location.DefaultLocationClient
import vaid.project.model.Groups
import vaid.project.model.User
import vaid.project.repository.Repository
import vaid.project.utils.Result
import java.util.UUID

class LocationViewModel(
    private val locationClient: DefaultLocationClient,
    private val repository: Repository
) : ViewModel() {

    var location: MutableLiveData<Result<Location>> = MutableLiveData<Result<Location>>()

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
            val user = User(name, 0.0, 0.0, null)
            val group = Groups(id, null, "Users")
            repository.signup(id, name, email, password)
            repository.addUser(user, id)
            repository.addGroup(group, ID.unique())
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

}