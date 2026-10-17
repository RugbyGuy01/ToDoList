package com.golfpvcc.todolist.lists.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

data class TodoListWithCounts(
    @Embedded val list: TodoListEntity,
    val totalTaskCount: Int,
    val doneTaskCount: Int
)

@Dao
interface TodoListDao {

    @Query(
        """
        SELECT l.*, COUNT(t.id) AS totalTaskCount, SUM(CASE WHEN t.is_done = 1 THEN 1 ELSE 0 END) AS doneTaskCount
        FROM todo_lists l
        LEFT JOIN tasks t ON t.listId = l.id
        GROUP BY l.id
        ORDER BY l.createdAt DESC
        """
    )
    fun getListsWithCounts(): Flow<List<TodoListWithCounts>>

    @Query("SELECT * FROM todo_lists WHERE id = :id")
    suspend fun getListById(id: Long): TodoListEntity?

    @Upsert
    suspend fun upsertList(list: TodoListEntity): Long

    @Query("DELETE FROM todo_lists WHERE id = :id")
    suspend fun deleteList(id: Long)
}