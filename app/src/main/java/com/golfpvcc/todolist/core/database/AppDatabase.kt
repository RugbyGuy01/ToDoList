package com.golfpvcc.todolist.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.golfpvcc.todolist.lists.data.TodoListDao
import com.golfpvcc.todolist.lists.data.TodoListEntity
import com.golfpvcc.todolist.tasks.data.TaskDao
import com.golfpvcc.todolist.tasks.data.TaskEntity

@Database(
    entities = [TodoListEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoListDao(): TodoListDao
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "todolist.db"
    }
}