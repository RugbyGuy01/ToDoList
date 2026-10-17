package com.golfpvcc.todolist.tasks.presentation

data class SearchState(
    val query: String = "",
    val results: List<SearchResultUi> = emptyList(),
    val isLoading: Boolean = false
)

data class SearchResultUi(
    val taskId: Long,
    val listId: Long,
    val title: String,
    val listName: String,
    val isDone: Boolean
)