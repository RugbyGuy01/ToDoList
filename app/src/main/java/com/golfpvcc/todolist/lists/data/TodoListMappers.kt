package com.golfpvcc.todolist.lists.data

import com.golfpvcc.todolist.lists.domain.TodoList
import com.golfpvcc.todolist.lists.domain.TodoListOverview

fun TodoListEntity.toTodoList(): TodoList = TodoList(
    id = id,
    name = name,
    createdAt = createdAt
)

fun TodoList.toTodoListEntity(): TodoListEntity = TodoListEntity(
    id = id,
    name = name,
    createdAt = createdAt
)

fun TodoListWithCounts.toTodoListOverview(): TodoListOverview = TodoListOverview(
    list = list.toTodoList(),
    totalTaskCount = totalTaskCount,
    doneTaskCount = doneTaskCount
)