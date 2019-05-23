package com.github.snuffix.songapp.presentation.model

/**
 * Model that will be used for passing data to UI module
 */
open class Resource<out T : Any> {

    class Success<T : Any>(val data: T) : Resource<T>()

    class Loading<T : Any> : Resource<T>()

    class Error<T : Any>(val message: String? = null) : Resource<T>()
}