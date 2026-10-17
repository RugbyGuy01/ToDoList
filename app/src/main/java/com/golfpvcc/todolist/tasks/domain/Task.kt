package com.golfpvcc.todolist.tasks.domain

data class Task(
    val id: Long = 0L,
    val listId: Long,
    val title: String,
    val description: String = "",
    val isDone: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Long? = null,
    val reminderAt: Long? = null,
    val createdAt: Long
)