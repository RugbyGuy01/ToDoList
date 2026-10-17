package com.golfpvcc.todolist.reminders

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.golfpvcc.todolist.tasks.domain.Task
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val workManager: WorkManager) {

    fun schedule(task: Task) {
        cancel(task.id)
        val reminderAt = task.reminderAt ?: return
        val delayMillis = reminderAt - System.currentTimeMillis()
        if (task.isDone || delayMillis <= 0) return

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    ReminderWorker.KEY_TASK_ID to task.id,
                    ReminderWorker.KEY_TASK_TITLE to task.title
                )
            )
            .build()

        workManager.enqueueUniqueWork(uniqueWorkName(task.id), ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(taskId: Long) {
        workManager.cancelUniqueWork(uniqueWorkName(taskId))
    }

    private fun uniqueWorkName(taskId: Long) = "reminder_$taskId"
}