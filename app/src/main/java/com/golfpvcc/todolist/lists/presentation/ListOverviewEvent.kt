package com.golfpvcc.todolist.lists.presentation

sealed interface ListOverviewEvent {
    data class NavigateToTasks(val listId: Long) : ListOverviewEvent
    data object NavigateToSearch : ListOverviewEvent
}