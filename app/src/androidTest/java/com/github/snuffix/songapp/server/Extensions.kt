package com.github.snuffix.songapp.server

import com.google.gson.Gson
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

const val MOCK_SERVER_PORT = 8080
const val TEST_SERVER_URL = "http://localhost:$MOCK_SERVER_PORT/"

fun MockWebServer.setResponses(vararg  responses: EndpointResponse) {
    dispatcher = object : Dispatcher() {

        private val gson = Gson()

        override fun dispatch(request: RecordedRequest) = MockResponse().apply {
            val response = responseFor(request)

            if (response is ErrorResponse) {
                setResponseCode(response.errorCode)
            }

            setJsonBody(response)
        }

        private fun responseFor(request: RecordedRequest): Any {
            val response = responses.firstOrNull { it.isFromPath(request.path) } ?: error("Unknown path $request")
            return response.body
        }

        private fun MockResponse.setJsonBody(body: Any) = setBody(gson.toJson(body))
    }
}

class EndpointResponse(val body: Any, val isFromPath: (String) -> Boolean)
class ErrorResponse(val errorCode: Int)
