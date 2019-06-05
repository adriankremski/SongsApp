package com.github.snuffix.songapp.domain.usecase

import com.github.snuffix.songapp.domain.model.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

abstract class BaseUseCase<DATA : Any, in Params>(private val retryLogic: BaseRetryLogic) {
    abstract suspend fun execute(params: Params): Result<DATA>

    open suspend fun executeWithRetry(
        params: Params,
        emitFailedResult: Boolean = false,
        maxRetries: Int = 3,
        initialDelay: Long = TimeUnit.SECONDS.toMillis(1),
        shouldRetry: Result<DATA>.() -> Boolean
    ): Flow<RetryResult<DATA>> = retryLogic.asFlow(
        params = params,
        emitFailedResult = emitFailedResult,
        maxRetries = maxRetries,
        initialDelay = initialDelay,
        execute = ::execute,
        shouldRetry = shouldRetry
    )
}

interface RetryLogic {
    fun <DATA : Any, Params> asFlow(
        params: Params,
        emitFailedResult: Boolean = false,
        maxRetries: Int = 3,
        initialDelay: Long = TimeUnit.SECONDS.toMillis(1),
        execute: suspend (Params) -> Result<DATA>,
        shouldRetry: Result<DATA>.() -> Boolean
    ): Flow<RetryResult<DATA>>
}

class BaseRetryLogic : RetryLogic {
    override fun <DATA : Any, Params> asFlow(
        params: Params,
        emitFailedResult: Boolean,
        maxRetries: Int,
        initialDelay: Long,
        execute: suspend (Params) -> Result<DATA>,
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

class RetryResult<T : Any>(val retryNumber: Int, val result: Result<T>)