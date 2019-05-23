package com.github.snuffix.songapp

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.snuffix.songapp.presentation.model.Resource

abstract class BaseFragment : Fragment() {
    fun <T : Any?> LiveData<T>.observe(onChanged: (T) -> Unit = {}) {
        observe(viewLifecycleOwner, Observer<T> {
            onChanged(it)
        })
    }

    fun <T : Any?> LiveData<T?>.observeNullable(onChanged: (T?) -> Unit = {}) {
        observe(viewLifecycleOwner, Observer {
            onChanged(it)
        })
    }

    fun <T : Any> LiveData<Resource<T>>.observe(
        onLoading: () -> Unit = {},
        onError: (Resource.Error<T>) -> Unit = {},
        onSuccess: (Resource.Success<T>) -> Unit = {}
    ) {
        observe(viewLifecycleOwner, Observer<Resource<T>> { resource ->
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