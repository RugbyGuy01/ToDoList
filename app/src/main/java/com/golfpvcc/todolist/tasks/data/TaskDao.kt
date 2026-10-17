package com.golfpvcc.todolist.tasks.data

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

data class TaskWithListName(
    @Embedded val task: TaskEntity,
    val listName: String
)

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE listId = :listId ORDER BY createdAt DESC")
    fun getTasksForList(listId: Long): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT t.*, l.name AS listName
        FROM tasks t
        INNER JOIN todo_lists l ON l.id = t.listId
        WHERE t.title LIKE '%' || :query || '%' OR t.description LIKE '%' || :query || '%'
        ORDER BY t.createdAt DESC
        """
    )
    fun searchTasks(query: String): Flow<List<TaskWithListName>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Upsert
    suspend fun upsertTask(task: TaskEntity): Long

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)

    @Query("UPDATE tasks SET is_done = :isDone WHERE id = :id")
    suspend fun setDone(id: Long, isDone: Boolean)
}