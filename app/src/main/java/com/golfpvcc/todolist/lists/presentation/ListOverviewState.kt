package com.golfpvcc.todolist.lists.presentation

data class ListOverviewState(
    val lists: List<TodoListUi> = emptyList(),
    val isLoading: Boolean = false,
    val newListDialogVisible: Boolean = false,
    val listPendingDeletion: TodoListUi? = null
)

data class TodoListUi(
    val id: Long,
    val name: String,
    val totalTaskCount: Int,
    val doneTaskCount: Int
)