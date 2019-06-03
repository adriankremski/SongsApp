package com.github.snuffix.songapp

import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.github.snuffix.songapp.server.TEST_SERVER_URL


@Suppress("unused") // Used from build.gradle.
class TestRunner : AndroidJUnitRunner() {
    override fun newApplication(loader: ClassLoader, name: String, context: Context) = super.newApplication(loader, TestApp::class.java.name, context)!!
}

class TestApp : SongsApp() {
    override val serverUrl: String
        get() = TEST_SERVER_URL
}



