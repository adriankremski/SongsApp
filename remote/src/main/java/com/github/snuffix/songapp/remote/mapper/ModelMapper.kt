package com.github.snuffix.songapp.remote.mapper


interface ModelMapper<in Model, out Entity> {
    fun mapFromModel(model: Model): Entity
}