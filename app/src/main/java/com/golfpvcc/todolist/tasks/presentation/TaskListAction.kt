package com.golfpvcc.todolist.tasks.presentation

import com.golfpvcc.todolist.tasks.domain.Priority
import com.golfpvcc.todolist.tasks.domain.TaskFilter
import com.golfpvcc.todolist.tasks.domain.TaskSort

sealed interface TaskListAction {
    data class OnFilterChange(val filter: TaskFilter) : TaskListAction
    data class OnSortChange(val sort: TaskSort) : TaskListAction
    data object OnAddTaskClick : TaskListAction
    data class OnTaskClick(val task: TaskUi) : TaskListAction
    data class OnToggleDone(val task: TaskUi) : TaskListAction
    data class OnDeleteTaskClick(val task: TaskUi) : TaskListAction
    data object OnConfirmDeleteTask : TaskListAction
    data object OnDismissDeleteTask : TaskListAction
    data object OnDismissEditor : TaskListAction
    data class OnSaveTask(
        val title: String,
        val description: String,
        val priority: Priority,
        val dueDate: Long?,
        val reminderAt: Long?
    ) : TaskListAction
}