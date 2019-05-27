package com.github.snuffix.songapp

import android.content.Context
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.runner.AndroidJUnitRunner
import com.github.snuffix.songapp.presentation.Launcher
import com.github.snuffix.songapp.server.TEST_SERVER_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Suppress("unused") // Used from build.gradle.
class TestRunner : AndroidJUnitRunner() {
    override fun newApplication(loader: ClassLoader, name: String, context: Context) = super.newApplication(loader, TestApp::class.java.name, context)!!
}

class TestApp : SongsApp() {

    override val coroutineLauncher: Launcher
        get() = TestLauncher()

    override val serverUrl: String
        get() = TEST_SERVER_URL
}

class TestLauncher : Launcher {
    override fun launch(scope: CoroutineScope, block: suspend CoroutineScope.() -> Unit) =
        scope.launch {
            coroutinesIdlingResource.increment()
            block()
            coroutinesIdlingResource.decrement()
        }
}

val coroutinesIdlingResource = CountingIdlingResource("CoroutineIdlingResource")


