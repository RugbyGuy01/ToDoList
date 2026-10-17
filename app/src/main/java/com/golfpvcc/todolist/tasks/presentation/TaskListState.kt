package com.golfpvcc.todolist.tasks.presentation

import com.golfpvcc.todolist.tasks.domain.Priority
import com.golfpvcc.todolist.tasks.domain.TaskFilter
import com.golfpvcc.todolist.tasks.domain.TaskSort

data class TaskListState(
    val listId: Long = 0L,
    val listName: String = "",
    val tasks: List<TaskUi> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val sort: TaskSort = TaskSort.CREATED_AT,
    val isLoading: Boolean = false,
    val editorVisible: Boolean = false,
    val editingTask: TaskUi? = null,
    val taskPendingDeletion: TaskUi? = null
)

data class TaskUi(
    val id: Long,
    val title: String,
    val description: String,
    val isDone: Boolean,
    val priority: Priority,
    val dueDate: Long?,
    val reminderAt: Long?,
    val formattedDueDate: String?,
    val formattedReminder: String?
)