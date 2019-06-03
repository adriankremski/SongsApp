package com.github.snuffix.songapp

import android.content.Context
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.runner.AndroidJUnitRunner
import com.github.snuffix.songapp.presentation.Launcher
import com.github.snuffix.songapp.server.TEST_SERVER_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.dsl.module


@Suppress("unused") // Used from build.gradle.
class TestRunner : AndroidJUnitRunner() {
    override fun newApplication(loader: ClassLoader, name: String, context: Context) = super.newApplication(loader, TestApp::class.java.name, context)!!

}

class TestApp : SongsApp() {
    override val serverUrl: String
        get() = TEST_SERVER_URL

    override val testModules = listOf(module {
        factory<Launcher>(override = true) {
            TestLauncher()
        }
    })
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



