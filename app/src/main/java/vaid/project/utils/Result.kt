package vaid.project.utils

import vaid.project.location.LocationClient

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: LocationClient.LocationException) : Result<Nothing>()
}