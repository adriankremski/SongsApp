package com.github.snuffix.songapp.domain.usecase

import com.github.snuffix.songapp.domain.model.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

abstract class BaseUseCase<DATA : Any, in Params> {
    abstract suspend fun buildUseCase(params: Params? = null): Result<DATA>

    open suspend fun execute(params: Params? = null) = buildUseCase(params)

    open suspend fun executeWithRetry(
        params: Params? = null,
        emitFailedResult: Boolean = false,
        maxRetries: Int = 3,
        initialDelay: Long = TimeUnit.SECONDS.toMillis(1),
        shouldRetry: Result<DATA>.() -> Boolean
    ): Flow<RetryResult<DATA>> = flow {

        var retryNumber = 0

        while (true) {
            val result = execute(params)

            if (result.shouldRetry() && retryNumber != maxRetries) {
                if (emitFailedResult) {
                    emit(RetryResult(retryNumber, result))
                }

                retryNumber++
                delay(initialDelay * (retryNumber + 1))
            } else {
                emit(RetryResult(retryNumber, result))

                break
            }
        }
    }
}

interface RetryLogic<DATA : Any, in Params> {
    fun getFlow(
        params: Params? = null,
        emitFailedResult: Boolean = false,
        maxRetries: Int = 3,
        initialDelay: Long = TimeUnit.SECONDS.toMillis(1),
        shouldRetry: Result<DATA>.() -> Boolean

    ): Flow<DATA>
}

class BaseRetryLogic<DATA : Any, in Params> : RetryLogic<DATA, Params> {
    override fun getFlow(
        params: Params?,
        emitFailedResult: Boolean,
        maxRetries: Int,
        initialDelay: Long,
        execute: suspend () -> Boolean
        shouldRetry: Result<DATA>.() -> Boolean
    ): Flow<DATA> = flow {

        var retryNumber = 0

        while (true) {
            val result = execute(params)

            if (result.shouldRetry() && retryNumber != maxRetries) {
                if (emitFailedResult) {
                    emit(RetryResult(retryNumber, result))
                }

                retryNumber++
                delay(initialDelay * (retryNumber + 1))
            } else {
                emit(RetryResult(retryNumber, result))

                break
            }
        }
    }
}


class RetryResult<T : Any>(val retryNumber: Int, val result: Result<T>)