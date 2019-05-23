package com.github.snuffix.songapp.presentation.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

inline fun <A, B> LiveData<A>.map(crossinline block: A.() -> B): LiveData<B> = Transformations.map(this) inner@{
    return@inner block.invoke(it)
}