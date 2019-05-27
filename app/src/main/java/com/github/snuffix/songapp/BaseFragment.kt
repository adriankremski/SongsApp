package com.github.snuffix.songapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.github.snuffix.songapp.presentation.model.Resource

abstract class BaseFragment : Fragment() {

    fun <T : ViewDataBinding> performDataBinding(
        layoutResID: Int,
        inflater: LayoutInflater, container: ViewGroup?,
        viewModel: ViewModel
    ): T {
        val viewDataBinding: T = DataBindingUtil.inflate(inflater, layoutResID, container, false)
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.setVariable(layoutResID, viewModel)
        return viewDataBinding
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