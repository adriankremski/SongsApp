package com.github.snuffix.songapp.utils

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


internal class DebouncingQueryTextListener(lifecycle: Lifecycle, private val textChange: (String) -> Unit) :
    SearchView.OnQueryTextListener {

    private var lastQuery: String = ""

    private var debouncePeriod: Long = 500

    private val coroutineScope = lifecycle.coroutineScope

    private var searchJob: Job? = null

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText == lastQuery) return false

        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            newText?.let {
                lastQuery = newText
                delay(debouncePeriod)
                textChange(newText)
            }
        }
        return false
    }
}