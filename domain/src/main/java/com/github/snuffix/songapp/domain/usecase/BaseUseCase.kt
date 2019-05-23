package com.github.snuffix.songapp.domain.usecase

import com.github.snuffix.songapp.domain.model.Result

abstract class BaseUseCase<DATA : Any, in Params> {
    abstract suspend fun buildUseCase(params: Params? = null): Result<DATA>
    open suspend fun execute(params: Params? = null) = buildUseCase(params)
}