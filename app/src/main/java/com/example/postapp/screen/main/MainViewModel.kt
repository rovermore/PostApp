package com.example.postapp.screen.main


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postapp.usecase.GetAllPostsUseCase
import com.example.postapp.utils.ViewState
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel
@Inject constructor(private val getAllPostsUseCase: GetAllPostsUseCase) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = _viewState

    init {
        loadData()
    }

    private fun loadData() {
        _viewState.value = ViewState.Loading
        setupObservers()
    }


    private fun setupObservers() {
        observeResponse()
    }

    private fun observeResponse() {
        viewModelScope.launch {
            val allPosts = getAllPostsUseCase.request()
            if (!allPosts.isNullOrEmpty())
                _viewState.value = ViewState.Content(allPosts)
            else
                _viewState.value = ViewState.Error
        }
    }
}