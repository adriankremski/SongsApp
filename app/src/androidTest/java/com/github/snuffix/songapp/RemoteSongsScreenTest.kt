package com.github.snuffix.songapp

import android.widget.AutoCompleteTextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.github.snuffix.songapp.model.SongDataFactory
import com.github.snuffix.songapp.remote.model.SongsResponse
import com.github.snuffix.songapp.server.EndpointResponse
import com.github.snuffix.songapp.server.ErrorResponse
import com.github.snuffix.songapp.server.MOCK_SERVER_PORT
import com.github.snuffix.songapp.server.setResponses
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers.greaterThan
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class RemoteSongsScreenTest {

    @get:Rule
    private val activityRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var server: MockWebServer

    private val searchResponse = SongsResponse(results = SongDataFactory.makeSongsList(1000))

    private val context
        get() = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(coroutinesIdlingResource)
        activityRule.launchActivity(null)

        setupMockServer(
            EndpointResponse(body = ErrorResponse(403)) { it.startsWith("/search") && it.contains("Eminem") },
            EndpointResponse(body = searchResponse) { it.startsWith("/search") })
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun remoteSongsScreenDisplaysFoundSongs() {
        openActionBarOverflowOrOptionsMenu(context)
        onView(withText(R.string.itunes_songs)).perform(click())
        onView(withId(R.id.songsRecycler))
            .perform(waitUntil(hasItemCount(greaterThan(0))))
            .check(matches(atPosition(0, hasDescendant(withText(searchResponse.results[0].trackName)))))
        onView(withId(R.id.errorView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.emptyView)).check(matches(not(isDisplayed())))
    }

    @Test
    fun remoteSongsScreenDisplaysTooManyRequestsError() {
        openActionBarOverflowOrOptionsMenu(context)
        onView(withText(R.string.itunes_songs)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText("Eminem"))
        onView(withId(R.id.errorView)).perform(waitUntil(isDisplayed()))
        onView(withId(R.id.errorView)).check(matches(hasDescendant(withText("Too many requests. Please wait"))))
    }

    private fun setupMockServer(vararg responses: EndpointResponse) = MockWebServer().apply {
        start(MOCK_SERVER_PORT)
        setResponses(*responses)
        server = this
    }
}