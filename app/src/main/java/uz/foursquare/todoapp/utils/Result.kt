package uz.foursquare.todoapp.utils

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure<T>(val exception: Throwable) : Result<T>()
    object Loading : Result<Nothing>()
}