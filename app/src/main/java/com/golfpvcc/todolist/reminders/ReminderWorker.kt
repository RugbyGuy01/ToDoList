package com.golfpvcc.todolist.reminders

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, -1L)
        val title = inputData.getString(KEY_TASK_TITLE) ?: return Result.failure()
        if (taskId == -1L) return Result.failure()

        NotificationHelper(applicationContext).showReminder(taskId, title, "Reminder for your task")
        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "task_id"
        const val KEY_TASK_TITLE = "task_title"
    }
}