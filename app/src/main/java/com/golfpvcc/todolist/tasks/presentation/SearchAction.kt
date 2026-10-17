package com.golfpvcc.todolist.tasks.presentation

sealed interface SearchAction {
    data class OnQueryChange(val query: String) : SearchAction
    data class OnResultClick(val result: SearchResultUi) : SearchAction
}