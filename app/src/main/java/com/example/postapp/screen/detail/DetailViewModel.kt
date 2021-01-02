package com.example.postapp.screen.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postapp.usecase.GetPostDetailsUseCase
import com.example.postapp.utils.ViewState
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class DetailViewModel
    @Inject constructor(private val getPostDetailsUseCase: GetPostDetailsUseCase) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = _viewState

    fun initialize(postId: Int) {
        loadData(postId)
    }

    private fun loadData(postId: Int) {
        _viewState.value = ViewState.Loading
        setupObservers(postId)
    }

    private fun setupObservers(postId: Int) {
        observeResponse(postId)
    }

    private fun observeResponse(postId: Int) {
        viewModelScope.launch {
            try {
                val postDetail = getPostDetailsUseCase.requestWithParameter(postId)
                _viewState.value = ViewState.Content(postDetail)
            } catch (e: Exception) {
                _viewState.value = ViewState.Error
            }
        }
    }

}