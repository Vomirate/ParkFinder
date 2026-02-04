package pl.edu.ur.wg131439.myapp.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.edu.ur.wg131439.myapp.di.AppGraph
import java.util.concurrent.TimeUnit

class ReminderActionsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_EXTEND_15 -> {
                // extend by 15 minutes: reschedule worker
                val req = OneTimeWorkRequestBuilder<ParkingReminderWorker>()
                    .setInitialDelay(15, TimeUnit.MINUTES)
                    .build()
                WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, req)

                val newAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)
                CoroutineScope(Dispatchers.IO).launch {
                    runCatching { AppGraph.repo.setReminderAt(newAt) }
                }
            }
            ACTION_DISMISS -> {
                WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
                AppGraph.notificationHelper.cancelReminderNotification()

                CoroutineScope(Dispatchers.IO).launch {
                    runCatching { AppGraph.repo.setReminderAt(null) }
                }
            }
        }
    }

    companion object {
        const val ACTION_EXTEND_15 = "pl.edu.ur.wg131439.myapp.ACTION_EXTEND_15"
        const val ACTION_DISMISS = "pl.edu.ur.wg131439.myapp.ACTION_DISMISS"
        const val WORK_NAME = "parking_reminder"

        fun pendingIntentExtend15(context: Context): PendingIntent {
            val intent = Intent(context, ReminderActionsReceiver::class.java).apply {
                action = ACTION_EXTEND_15
            }
            return PendingIntent.getBroadcast(
                context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun pendingIntentDismiss(context: Context): PendingIntent {
            val intent = Intent(context, ReminderActionsReceiver::class.java).apply {
                action = ACTION_DISMISS
            }
            return PendingIntent.getBroadcast(
                context, 2, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
