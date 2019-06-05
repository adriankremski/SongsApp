package com.github.snuffix.songapp

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.snuffix.songapp.presentation.model.ErrorType
import com.github.snuffix.songapp.presentation.model.Event
import com.github.snuffix.songapp.presentation.model.Resource

abstract class BaseFragment : Fragment() {

    fun <T : Any> LiveData<Event<T>>.observe(
        onChanged: (T) -> Unit = {}
    ) {
        observe(viewLifecycleOwner, Observer<Event<T>> { event ->
            event.getContentIfNotHandled()?.let(onChanged)
        })
    }

    fun <T : Any> LiveData<Resource<T>>.observe(
        onLoading: () -> Unit = {},
        onError: (String?, ErrorType) -> Unit = { message, errorType -> },
        onSuccess: (T) -> Unit = {}
    ) {
        observe(viewLifecycleOwner, Observer<Resource<T>> { resource ->
            resource?.let {
                when (resource) {
                    is Resource.Loading -> {
                        onLoading()
                    }
                    is Resource.Error -> {
                        onError(resource.message, resource.errorType)
                    }
                    is Resource.Success -> {
                        onSuccess(resource.data)
                    }
                }
            }
        })
    }
}