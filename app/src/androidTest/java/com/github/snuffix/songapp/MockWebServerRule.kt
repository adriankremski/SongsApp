package com.github.snuffix.songapp

import com.github.snuffix.songapp.server.MOCK_SERVER_PORT
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement


class MockWebServerRule : TestRule {

    val server = MockWebServer()

    override fun apply(base: Statement?, description: Description?) = object : Statement() {
        override fun evaluate() {
            server.start(MOCK_SERVER_PORT)
            base?.evaluate()
            server.shutdown()
        }
    }
}