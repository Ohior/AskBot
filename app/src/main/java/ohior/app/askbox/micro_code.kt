package ohior.app.askbox

import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log

fun debugMessage(message: String?, tag: String = "DEBUG MESSAGE") {
    Log.e(tag, "debugMessage: $message")
}


fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nw = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        //for other device how are able to connect with Ethernet
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        //for check internet over Bluetooth
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}

fun isNotificationDisplayed(context: Context): Boolean {
    val activeNotification =
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).activeNotifications
    for (notification in activeNotification) {
        if (notification.id == 1) {
            return true
        }
    }
    return false
}