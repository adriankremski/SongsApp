package com.github.snuffix.songapp.cache.mapper


interface RawModelMapper<in RawModel, out Model> {
    fun mapFromRawModel(model: RawModel): Model
}