package com.github.snuffix.songapp.presentation.model


/**
 * When using livedata for passing events, sometimes we want them to be handled only once (this class
 * helps with such use case)
 */
@Suppress("unused")
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}