package com.golfpvcc.todolist.tasks.data

import com.golfpvcc.todolist.core.domain.DataError
import com.golfpvcc.todolist.core.domain.EmptyResult
import com.golfpvcc.todolist.core.domain.Result
import com.golfpvcc.todolist.reminders.ReminderScheduler
import com.golfpvcc.todolist.tasks.domain.Task
import com.golfpvcc.todolist.tasks.domain.TaskLocalDataSource
import com.golfpvcc.todolist.tasks.domain.TaskSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTaskDataSource(
    private val dao: TaskDao,
    private val reminderScheduler: ReminderScheduler
) : TaskLocalDataSource {

    override fun getTasksForList(listId: Long): Flow<List<Task>> {
        return dao.getTasksForList(listId).map { it.map { entity -> entity.toTask() } }
    }

    override fun searchTasks(query: String): Flow<List<TaskSearchResult>> {
        return dao.searchTasks(query).map { it.map { entry -> entry.toTaskSearchResult() } }
    }

    override suspend fun getTaskById(id: Long): Result<Task, DataError.Local> {
        return try {
            val entity = dao.getTaskById(id) ?: return Result.Error(DataError.Local.NOT_FOUND)
            Result.Success(entity.toTask())
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun upsertTask(task: Task): Result<Long, DataError.Local> {
        return try {
            val id = dao.upsertTask(task.toTaskEntity())
            val savedId = if (id != -1L) id else task.id
            reminderScheduler.schedule(task.copy(id = savedId))
            Result.Success(savedId)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun deleteTask(id: Long): EmptyResult<DataError.Local> {
        return try {
            dao.deleteTask(id)
            reminderScheduler.cancel(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun setDone(id: Long, isDone: Boolean): EmptyResult<DataError.Local> {
        return try {
            dao.setDone(id, isDone)
            if (isDone) reminderScheduler.cancel(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}