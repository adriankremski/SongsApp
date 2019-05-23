package com.github.snuffix.songapp.presentation.model

/**
 * Model that will be used for passing data to UI module
 */
open class Resource<out T : Any> {

    class Success<T : Any>(val data: T) : Resource<T>()

    class Loading<T : Any> : Resource<T>()

    class Error<T : Any>(val message: String? = null, errorType: ErrorType? = null) : Resource<T>()

    companion object {
        fun <T : Any> error(message: String? = null) = Resource.Error<T>(message, ErrorType.ERROR)
        fun <T : Any> networkError(message: String? = null) = Resource.Error<T>(message, ErrorType.NETWORK)
        fun <T : Any> apiError(message: String? = null) = Resource.Error<T>(message, ErrorType.API)
    }
}

enum class ErrorType {
    NETWORK, API, ERROR
}