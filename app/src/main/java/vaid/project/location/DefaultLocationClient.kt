package vaid.project.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import vaid.project.utils.Result
import vaid.project.utils.hasLocationPermission


class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Result<Location>> {
        return callbackFlow {

            // Проверяем наличие разрешений на доступ к местоположению.
            if (!context.hasLocationPermission()) {
                send(Result.Error(LocationClient.LocationException.MissingLocationPermissionException()))
                return@callbackFlow
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            // включен ли GPS-провайдер и интернет.
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {
                send(Result.Error(LocationClient.LocationException.GPSDisabledException()))
                return@callbackFlow
            }

            //настройка параментров получения местоположения
            val request = LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(interval)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

            //обработка результатов местоположения.
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)

                    //отправляем местоположение
                    result.lastLocation.let { location ->
                        launch {
                            send(Result.Success(location))
                        }
                    }
                }

                override fun onLocationAvailability(locationAv: LocationAvailability) {
                    super.onLocationAvailability(locationAv)

                    if (!locationAv.isLocationAvailable) {
                        launch {
                            send(Result.Error(LocationClient.LocationException.LocationUnavailable()))
                        }
                    }
                }
            }

            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper() //Обновления будут поступать в главном потоке
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}
