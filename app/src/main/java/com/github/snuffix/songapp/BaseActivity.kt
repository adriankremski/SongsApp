package com.github.snuffix.songapp

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.snuffix.songapp.presentation.model.Resource


abstract class BaseActivity : AppCompatActivity() {

    fun <T : Any?> LiveData<T>.observe(onChanged: (T) -> Unit = {}) {
        observe(this@BaseActivity, Observer<T> {
            onChanged(it)
        })
    }

    fun <T : Any?> LiveData<T?>.observeNullable(onChanged: (T?) -> Unit = {}) {
        observe(this@BaseActivity, Observer {
            onChanged(it)
        })
    }

    fun <T : Any> LiveData<Resource<T>>.observe(
        onLoading: () -> Unit = {},
        onError: (Resource.Error<T>) -> Unit = {},
        onSuccess: (Resource.Success<T>) -> Unit = {}
    ) {
        observe(this@BaseActivity, Observer<Resource<T>> { resource ->
            resource?.let {
                when (resource) {
                    is Resource.Loading -> {
                        onLoading()
                    }
                    is Resource.Error -> {
                        onError(resource)
                    }
                    is Resource.Success -> {
                        onSuccess(resource)
                    }
                }
            }
        })
    }
}