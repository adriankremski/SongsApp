package com.github.snuffix.songapp.domain.model

/**
 * Wrapper class for passing data between 'data', 'presentation' and 'domain' modules
 */
@Suppress("unused")
sealed class Result<out T : Any> {
    var isHandled = false

    class Ok<out T : Any>(val value: T) : Result<T>()
    class ApiError(val code: Int, val message: String? = null) : Error()
    class NetworkError(exception: Exception? = null) : Error(exception)
    class CancelledError : Error()
    open class Error(val exception: Exception? = null) : Result<Nothing>()

    inline fun whenOk(block: Ok<T>.() -> Unit): Result<T> {
        if (this is Ok) {
            this.block()
        }
        return this
    }

    inline fun whenError(block: Error.() -> Unit): Result<T> {
        if (!this.isHandled && this is Error && this !is CancelledError) {
            this.block()
            this.isHandled = true
        }
        return this
    }

    inline fun <reified V : Any> whenOkReturn(mapResult: (T) -> V): Result<V> = if (this is Ok) {
        Ok(mapResult(this.value))
    } else {
        castErrorResult(this as Error)
    }
}

class CombinedResult<T : Any>(private vararg val results: Result<T>) {
    fun isOk() = results.count { it is Result.Ok } == results.size
    fun firstErrorResult() = results.first { it is Result.Error } as Result.Error
}

inline fun <reified T : Any> castErrorResult(source: Result.Error): Result<T> {
    return when (source) {
        is Result.ApiError -> {
            Result.ApiError(source.code, source.message)
        }
        is Result.NetworkError -> {
            Result.NetworkError(source.exception)
        }
        is Result.CancelledError -> {
            Result.CancelledError()
        }
        is Result.Error -> {
            Result.Error(source.exception)
        }
        else -> {
            throw IllegalArgumentException("Incorrect type")
        }
    }
}

