package vaid.project.location

import android.location.Location
import kotlinx.coroutines.flow.Flow
import vaid.project.utils.Result

interface LocationClient {

    fun getLocationUpdates(interval: Long): Flow<Result<Location>>

    sealed class LocationException(val error: String) : Exception(){
        class GPSDisabledException : LocationException("Location services is disabled")
        class MissingLocationPermissionException : LocationException("Missing location permissions")
        class LocationUnavailable : LocationException("Location is unavailable")
    }
}