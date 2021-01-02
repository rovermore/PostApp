package com.example.postapp.utils

sealed class ViewState {
    object Loading: ViewState()
    object Error: ViewState()
    data class Content<T>(val data: T): ViewState()
}