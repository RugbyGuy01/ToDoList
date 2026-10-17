package com.golfpvcc.todolist.lists.di

import com.golfpvcc.todolist.lists.data.RoomTodoListDataSource
import com.golfpvcc.todolist.lists.domain.TodoListLocalDataSource
import com.golfpvcc.todolist.lists.presentation.ListOverviewViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val listsDataModule = module {
    singleOf(::RoomTodoListDataSource) { bind<TodoListLocalDataSource>() }
}

val listsPresentationModule = module {
    viewModelOf(::ListOverviewViewModel)
}