package com.golfpvcc.todolist.lists.data

import com.golfpvcc.todolist.core.domain.DataError
import com.golfpvcc.todolist.core.domain.EmptyResult
import com.golfpvcc.todolist.core.domain.Result
import com.golfpvcc.todolist.lists.domain.TodoList
import com.golfpvcc.todolist.lists.domain.TodoListLocalDataSource
import com.golfpvcc.todolist.lists.domain.TodoListOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTodoListDataSource(private val dao: TodoListDao) : TodoListLocalDataSource {

    override fun getLists(): Flow<List<TodoListOverview>> {
        return dao.getListsWithCounts().map { it.map { entry -> entry.toTodoListOverview() } }
    }

    override suspend fun getListById(id: Long): Result<TodoList, DataError.Local> {
        return try {
            val entity = dao.getListById(id) ?: return Result.Error(DataError.Local.NOT_FOUND)
            Result.Success(entity.toTodoList())
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun upsertList(list: TodoList): Result<Long, DataError.Local> {
        return try {
            val id = dao.upsertList(list.toTodoListEntity())
            Result.Success(if (id != -1L) id else list.id)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun deleteList(id: Long): EmptyResult<DataError.Local> {
        return try {
            dao.deleteList(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}