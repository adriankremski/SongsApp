package com.github.snuffix.songapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent

abstract class BaseViewModel : ViewModel(), KoinComponent {

    private val idlingResource = CountingIdlingResource("CoroutineIdlingResource")

    fun viewModelScopeLaunch(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch {
        idlingResource.increment()
        block()
        idlingResource.decrement()
    }
}

