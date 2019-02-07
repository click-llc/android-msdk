package uz.click.mobilesdk.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
object LanguageUtils {
    fun getLocaleStringResource(requestedLocale: Locale, resourceId: Int, context: Context): String {
        val result: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val config = Configuration(context.resources.configuration)
            config.setLocale(requestedLocale)
            result = context.createConfigurationContext(config).getText(resourceId).toString()
        } else {
            val resources = context.resources
            val conf = resources.configuration
            val savedLocale = conf.locale
            conf.locale = requestedLocale
            resources.updateConfiguration(conf, null)
            result = resources.getString(resourceId)
            conf.locale = savedLocale
            resources.updateConfiguration(conf, null)
        }
        return result
    }
}