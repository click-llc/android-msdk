package uz.click.mobilesdk.core

import android.support.annotation.Keep
import android.support.v4.app.FragmentManager
import uz.click.mobilesdk.core.callbacks.ClickMerchantListener
import uz.click.mobilesdk.impl.MainDialogFragment

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
object ClickMerchant {

    private val TAG_BOTTOM_SHEET = MainDialogFragment::class.java.name
    private lateinit var supportFragmentManager: FragmentManager

    @[JvmStatic Keep]
    fun init(supportFragmentManager: FragmentManager, config: ClickMerchantConfig, listener: ClickMerchantListener) {
        this.supportFragmentManager = supportFragmentManager
        if (findDialog(supportFragmentManager) == null) {
            val dialog = MainDialogFragment.newInstance(config)
            dialog.setListener(listener)
            dialog.show(supportFragmentManager, TAG_BOTTOM_SHEET)
        }
    }

    private fun findDialog(supportFragmentManager: FragmentManager) =
        supportFragmentManager.findFragmentByTag(TAG_BOTTOM_SHEET) as MainDialogFragment?
}