package com.golfpvcc.todolist.reminders.di

import androidx.work.WorkManager
import com.golfpvcc.todolist.reminders.NotificationHelper
import com.golfpvcc.todolist.reminders.ReminderScheduler
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val reminderModule = module {
    singleOf(::NotificationHelper)
    single { WorkManager.getInstance(get()) }
    singleOf(::ReminderScheduler)
}