package com.golfpvcc.todolist

import android.app.Application
import com.golfpvcc.todolist.core.di.coreModule
import com.golfpvcc.todolist.lists.di.listsDataModule
import com.golfpvcc.todolist.lists.di.listsPresentationModule
import com.golfpvcc.todolist.reminders.NotificationHelper
import com.golfpvcc.todolist.reminders.di.reminderModule
import com.golfpvcc.todolist.tasks.di.tasksDataModule
import com.golfpvcc.todolist.tasks.di.tasksPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                coreModule,
                reminderModule,
                listsDataModule,
                listsPresentationModule,
                tasksDataModule,
                tasksPresentationModule
            )
        }

        NotificationHelper(this).createChannel()
    }
}