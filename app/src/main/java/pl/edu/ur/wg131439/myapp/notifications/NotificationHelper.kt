package pl.edu.ur.wg131439.myapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pl.edu.ur.wg131439.myapp.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "parkfinder_reminders"
        const val NOTIF_ID = 1001
    }

    fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notif_channel_desc)
            }
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mgr.createNotificationChannel(channel)
        }
    }

    fun showReminderNotification() {
        ensureChannel()

        val extendIntent = ReminderActionsReceiver.pendingIntentExtend15(context)
        val dismissIntent = ReminderActionsReceiver.pendingIntentDismiss(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(context.getString(R.string.notif_text))
            .setAutoCancel(true)
            .addAction(0, context.getString(R.string.action_extend_15), extendIntent)
            .addAction(0, context.getString(R.string.action_dismiss), dismissIntent)

        NotificationManagerCompat.from(context).notify(NOTIF_ID, builder.build())
    }

    fun cancelReminderNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIF_ID)
    }
}
