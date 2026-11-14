package com.example.muse.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object BackupScheduler {

    private const val WORK_NAME = "muse_copy_chain"

    /** Немедленный запуск: APPEND_OR_REPLACE, не отменяет выполняющуюся работу. */
    fun scheduleCopy(context: Context) {
        val request = OneTimeWorkRequestBuilder<CopyAllWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, MIN_BACKOFF_SECONDS, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
    }

    /**
     * Отложенный запуск: REPLACE, чтобы более ранний nextAttemptAt заменил более поздний.
     *
     * ВАЖНО: вызывать только в самом конце doWork(), после завершения всех операций.
     * REPLACE отменит текущий Worker (best-effort), но doWork уже вернёт success().
     */
    fun scheduleDelayedCopy(context: Context, delayMs: Long) {
        if (delayMs <= 0L) return
        val request = OneTimeWorkRequestBuilder<CopyAllWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    private const val MIN_BACKOFF_SECONDS = 30L
}