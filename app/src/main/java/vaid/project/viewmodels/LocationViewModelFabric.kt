package vaid.project.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vaid.project.location.DefaultLocationClient

class LocationViewModelFactory(private val locationClient: DefaultLocationClient) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            return LocationViewModel(locationClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}