package com.golfpvcc.todolist.tasks.presentation

import kotlinx.serialization.Serializable

@Serializable
data class TasksRoute(val listId: Long)

@Serializable
object SearchRoute