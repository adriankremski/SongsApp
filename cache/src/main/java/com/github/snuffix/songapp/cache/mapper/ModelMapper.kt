package com.github.snuffix.songapp.cache.mapper


interface ModelMapper<in Model, out Entity> {
    fun mapFromModel(model: Model): Entity
}