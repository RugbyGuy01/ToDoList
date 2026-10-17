package com.golfpvcc.todolist.tasks.presentation

sealed interface TaskListEvent {
    data object ListNotFound : TaskListEvent
}