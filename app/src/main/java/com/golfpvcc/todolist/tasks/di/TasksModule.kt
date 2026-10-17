package com.golfpvcc.todolist.tasks.di

import com.golfpvcc.todolist.tasks.data.RoomTaskDataSource
import com.golfpvcc.todolist.tasks.domain.TaskLocalDataSource
import com.golfpvcc.todolist.tasks.presentation.SearchViewModel
import com.golfpvcc.todolist.tasks.presentation.TaskListViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tasksDataModule = module {
    singleOf(::RoomTaskDataSource) { bind<TaskLocalDataSource>() }
}

val tasksPresentationModule = module {
    viewModelOf(::TaskListViewModel)
    viewModelOf(::SearchViewModel)
}