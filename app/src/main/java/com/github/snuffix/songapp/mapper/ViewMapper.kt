package com.github.snuffix.songapp.mapper

interface ViewMapper<in PresentationModel, out UIModel> {
    fun mapToUIModel(presentation: PresentationModel): UIModel
}