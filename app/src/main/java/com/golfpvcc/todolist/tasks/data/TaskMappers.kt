package com.golfpvcc.todolist.tasks.data

import com.golfpvcc.todolist.tasks.domain.Priority
import com.golfpvcc.todolist.tasks.domain.Task
import com.golfpvcc.todolist.tasks.domain.TaskSearchResult

fun TaskEntity.toTask(): Task = Task(
    id = id,
    listId = listId,
    title = title,
    description = description,
    isDone = isDone,
    priority = Priority.entries[priority],
    dueDate = dueDate,
    reminderAt = reminderAt,
    createdAt = createdAt
)

fun Task.toTaskEntity(): TaskEntity = TaskEntity(
    id = id,
    listId = listId,
    title = title,
    description = description,
    isDone = isDone,
    priority = priority.ordinal,
    dueDate = dueDate,
    reminderAt = reminderAt,
    createdAt = createdAt
)

fun TaskWithListName.toTaskSearchResult(): TaskSearchResult = TaskSearchResult(
    task = task.toTask(),
    listName = listName
)