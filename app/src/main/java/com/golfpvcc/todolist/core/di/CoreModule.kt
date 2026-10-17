package com.golfpvcc.todolist.core.di

import androidx.room.Room
import com.golfpvcc.todolist.core.database.AppDatabase
import org.koin.dsl.module

val coreModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()
    }
    single { get<AppDatabase>().todoListDao() }
    single { get<AppDatabase>().taskDao() }
}