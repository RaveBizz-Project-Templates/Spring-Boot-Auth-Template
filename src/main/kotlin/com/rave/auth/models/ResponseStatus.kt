package com.rave.auth.models

sealed class ResponseStatus<out T> {
  data class Success<T>(val data: T): ResponseStatus<T>()
  data class Error(val message: String): ResponseStatus<Nothing>()
}
