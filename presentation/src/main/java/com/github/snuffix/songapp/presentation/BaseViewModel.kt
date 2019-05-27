package com.github.snuffix.songapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseViewModel(private val uiScopeLauncher: Launcher) : ViewModel() {
    fun viewModelScopeLaunch(block: suspend CoroutineScope.() -> Unit): Job? = uiScopeLauncher.launch(viewModelScope, block)
}

/*
 * Launches coroutine. Default implementation just delegates coroutine to the scope.
 */
interface Launcher {

    fun launch(scope: CoroutineScope, block: suspend CoroutineScope.() -> Unit): Job

    class Default : Launcher {
        override fun launch(scope: CoroutineScope, block: suspend CoroutineScope.() -> Unit) = scope.launch { block() }
    }
}
