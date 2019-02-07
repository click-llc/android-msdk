package uz.click.mobilesdk.utils

import android.content.Context
import android.content.pm.PackageManager

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
object ContextUtils {

    fun isAppAvailable(context: Context, appName: String): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }
}