package vaid.project.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vaid.project.location.DefaultLocationClient
import vaid.project.repository.Repository

class LocationViewModelFactory(
    private val locationClient: DefaultLocationClient,
    private val repository: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            return LocationViewModel(locationClient, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}