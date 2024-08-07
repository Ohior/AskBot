package ohior.app.askbox.service.ask_notification

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ohior.app.askbox.isNetworkAvailable
import ohior.app.askbox.service.AskBotAI

class AskNotificationReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {

        if (isNetworkAvailable(context)) {
            val replyText = RemoteInput.getResultsFromIntent(intent)
                ?.getCharSequence(AskNotification.REMOTE_INPUT_KEY)
            val service = AskNotification(context)
            val clear = (replyText?.trim().contentEquals("clear", ignoreCase = true) ||
                    replyText?.trim().contentEquals("clean", ignoreCase = true) ||
                    replyText?.trim().contentEquals("remove", ignoreCase = true))

            if (clear) {
                service.showNotification("enter new query")
            } else {
                scope.launch {
                    try {
                        val respond = AskBotAI.getQueryFromAI(replyText.toString())
                        service.showNotification(respond.text ?: "Could not get a response")
                    } catch (e: Exception) {
                        service.showNotification("Could not get a response")
                    }
                }
            }
        }
    }
}