package com.github.snuffix.songapp.presentation.mapper

interface Mapper<out PresentationModel, in DomainModel> {
    fun mapToView(type: DomainModel): PresentationModel
}