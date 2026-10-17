package com.golfpvcc.todolist.lists.domain

import com.golfpvcc.todolist.core.domain.DataError
import com.golfpvcc.todolist.core.domain.EmptyResult
import com.golfpvcc.todolist.core.domain.Result
import kotlinx.coroutines.flow.Flow

data class TodoListOverview(
    val list: TodoList,
    val totalTaskCount: Int,
    val doneTaskCount: Int
)

interface TodoListLocalDataSource {
    fun getLists(): Flow<List<TodoListOverview>>
    suspend fun getListById(id: Long): Result<TodoList, DataError.Local>
    suspend fun upsertList(list: TodoList): Result<Long, DataError.Local>
    suspend fun deleteList(id: Long): EmptyResult<DataError.Local>
}