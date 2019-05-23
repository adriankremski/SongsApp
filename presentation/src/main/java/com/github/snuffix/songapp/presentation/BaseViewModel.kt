package com.github.snuffix.songapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.snuffix.songapp.presentation.extensions.map
import com.github.snuffix.songapp.presentation.model.Event

abstract class BaseViewModel : ViewModel() {

    protected val showToastResource: MutableLiveData<Event<String>> = MutableLiveData()
    val showToast = showToastResource.map {
        this
    }
}

