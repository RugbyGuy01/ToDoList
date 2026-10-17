package com.golfpvcc.todolist.tasks.presentation

sealed interface SearchEvent {
    data class NavigateToList(val listId: Long) : SearchEvent
}