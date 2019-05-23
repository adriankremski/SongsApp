package com.github.snuffix.songapp.presentation.model

open class Resource<out T : Any> {

    class Success<T : Any>(val data: T) : Resource<T>()

    class Loading<T : Any> : Resource<T>()

    class Error<T : Any>(val message: String? = null) : Resource<T>()
}