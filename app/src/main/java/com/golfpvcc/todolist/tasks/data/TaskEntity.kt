package com.golfpvcc.todolist.tasks.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.golfpvcc.todolist.lists.data.TodoListEntity

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = TodoListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val listId: Long,
    val title: String,
    val description: String,
    @ColumnInfo(name = "is_done") val isDone: Boolean,
    val priority: Int,
    val dueDate: Long?,
    val reminderAt: Long?,
    val createdAt: Long
)