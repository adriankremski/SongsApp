package com.github.snuffix.songapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.snuffix.songapp.presentation.model.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent

abstract class BaseViewModel(launcherFactory: LauncherFactory) : ViewModel(), KoinComponent {
    val uiScope = launcherFactory.createLauncher(viewModelScope)
}

interface LauncherFactory {
    fun createLauncher(scope: CoroutineScope): Launcher
}

class DefaultLauncherFactory : LauncherFactory {
    override fun createLauncher(scope: CoroutineScope) = Launcher.Default(scope)
}

/*
 * Launches coroutine. Default implementation just delegates coroutine to the scope.
 */
interface Launcher {

    fun launch(block: suspend CoroutineScope.() -> Unit): Job

    class Default(private val scope: CoroutineScope) : Launcher {

        override fun launch(block: suspend CoroutineScope.() -> Unit) = scope.launch { block() }
    }
}

