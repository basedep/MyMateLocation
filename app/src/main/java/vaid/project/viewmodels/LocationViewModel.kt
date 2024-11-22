package vaid.project.viewmodels

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import vaid.project.location.DefaultLocationClient
import vaid.project.utils.Result

class LocationViewModel(private val locationClient: DefaultLocationClient): ViewModel() {

    var location: MutableLiveData<Result<Location>>  = MutableLiveData<Result<Location>>()

    init {
        getCurrentLocation()
    }

    private fun getCurrentLocation() = viewModelScope.launch {
        locationClient.getLocationUpdates(5000)
            .collect { result ->
                location.value = result
            }
    }

}