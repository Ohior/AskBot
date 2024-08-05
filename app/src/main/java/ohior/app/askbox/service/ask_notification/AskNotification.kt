package ohior.app.askbox.service.ask_notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import ohior.app.askbox.MainActivity
import ohior.app.askbox.R
import ohior.app.askbox.service.database.PrefManager

class AskNotification(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(message: CharSequence = "") {

        // ASK NOTIFICATION
        // *******************************************************************************
        val replyLabel = "Enter your reply here"
        val remoteInput =
            androidx.core.app.RemoteInput.Builder(REMOTE_INPUT_KEY)
                .setLabel(replyLabel)
                .build()

        val askPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            Intent(context, AskNotificationReceiver::class.java),
            PendingIntent.FLAG_MUTABLE
        )

        val action = NotificationCompat.Action.Builder(null, "Ask Query", askPendingIntent)
            .addRemoteInput(remoteInput).build()

        val activityIntent = Intent(context, MainActivity::class.java)

        PrefManager.saveData(PrefManager.BOT_RESULT_KEY, message.toString())

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )



        val notification = NotificationCompat.Builder(context, ASK_CHANNEL_ID)
            .setSmallIcon(R.drawable.chat_ai)
            .setContentTitle("Ask AI Bot")
            .addAction(action)
            .setContentIntent(activityPendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message.ifBlank { "No Result Found" })
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    companion object {
        const val ASK_CHANNEL_ID = "ask_channel_id"
        const val NOTIFICATION_ID = 1
        const val REMOTE_INPUT_KEY = "ask_remote_input_key"
    }
}