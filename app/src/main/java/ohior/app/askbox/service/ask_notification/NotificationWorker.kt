package ohior.app.askbox.service.ask_notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import ohior.app.askbox.isNetworkAvailable
import ohior.app.askbox.isNotificationDisplayed

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    private val service = AskNotification(applicationContext)
    override fun doWork(): Result {
        if (isNetworkAvailable(context) && !isNotificationDisplayed(context)) {
            service.showNotification()
        }
        return Result.success()
    }
}