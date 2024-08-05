package ohior.app.askbox.service

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

object PermissionManager {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissions = arrayOf(android.Manifest.permission.POST_NOTIFICATIONS)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermissionGranted(context: Context, granted: (Boolean,MutableList<String>) -> Unit) {
        var isGranted = true
        val permits = mutableListOf<String>()
        for (p in permissions) {
            if (
                ActivityCompat.checkSelfPermission(context, p) ==
                PackageManager.PERMISSION_DENIED
                ) {
                isGranted = false
                permits.add(p)
            }
        }
        granted(isGranted,permits)
    }

}