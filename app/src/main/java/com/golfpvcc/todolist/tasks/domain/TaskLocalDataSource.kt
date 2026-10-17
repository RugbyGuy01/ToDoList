package com.golfpvcc.todolist.tasks.domain

import com.golfpvcc.todolist.core.domain.DataError
import com.golfpvcc.todolist.core.domain.EmptyResult
import com.golfpvcc.todolist.core.domain.Result
import kotlinx.coroutines.flow.Flow

data class TaskSearchResult(
    val task: Task,
    val listName: String
)

interface TaskLocalDataSource {
    fun getTasksForList(listId: Long): Flow<List<Task>>
    fun searchTasks(query: String): Flow<List<TaskSearchResult>>
    suspend fun getTaskById(id: Long): Result<Task, DataError.Local>
    suspend fun upsertTask(task: Task): Result<Long, DataError.Local>
    suspend fun deleteTask(id: Long): EmptyResult<DataError.Local>
    suspend fun setDone(id: Long, isDone: Boolean): EmptyResult<DataError.Local>
}