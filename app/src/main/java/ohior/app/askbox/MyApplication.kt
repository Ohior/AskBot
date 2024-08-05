package ohior.app.askbox

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import ohior.app.askbox.service.database.PrefManager
import ohior.app.askbox.service.ask_notification.AskNotification
import ohior.app.askbox.service.database.ObjectBox

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PrefManager.initialize(applicationContext)
        ObjectBox.init(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val askChannel = NotificationChannel(
                AskNotification.ASK_CHANNEL_ID,
                "Ask Bot",
                NotificationManager.IMPORTANCE_HIGH
            )
            askChannel.description = "This notification is use to query the Bot. Ask Bot question through this notification"
            notificationManager.createNotificationChannel(askChannel)
        }
    }
}