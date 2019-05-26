package com.github.snuffix.songapp.data.mapper

interface EntityMapper<Entity, DomainModel> {
    fun mapFromEntity(entity: Entity, isFromRemote: Boolean): DomainModel
}