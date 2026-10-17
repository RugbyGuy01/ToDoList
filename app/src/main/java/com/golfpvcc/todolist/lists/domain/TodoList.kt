package com.golfpvcc.todolist.lists.domain

data class TodoList(
    val id: Long = 0L,
    val name: String,
    val createdAt: Long
)