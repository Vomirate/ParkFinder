package pl.edu.ur.wg131439.myapp.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pl.edu.ur.wg131439.myapp.di.AppGraph

class ParkingReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        AppGraph.notificationHelper.showReminderNotification()
        return Result.success()
    }
}
