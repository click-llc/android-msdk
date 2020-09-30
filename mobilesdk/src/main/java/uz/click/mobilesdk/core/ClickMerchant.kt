package uz.click.mobilesdk.core

import androidx.annotation.Keep
import androidx.fragment.app.FragmentManager
import uz.click.mobilesdk.core.callbacks.ClickMerchantListener
import uz.click.mobilesdk.impl.MainDialogFragment

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
object ClickMerchant {

    private val TAG_BOTTOM_SHEET = MainDialogFragment::class.java.name
    private lateinit var supportFragmentManager: androidx.fragment.app.FragmentManager

    @[JvmStatic Keep]
    fun init(
        supportFragmentManager: androidx.fragment.app.FragmentManager,
        config: ClickMerchantConfig,
        listener: ClickMerchantListener
    ) {
        this.supportFragmentManager = supportFragmentManager
        if (findDialog(supportFragmentManager) == null) {
            val dialog = MainDialogFragment.newInstance(config)
            dialog.setListener(listener)
            dialog.show(supportFragmentManager, TAG_BOTTOM_SHEET)
        }
    }

    @[JvmStatic Keep]
    fun dismiss() {
        findDialog(supportFragmentManager)?.dismiss()
    }

    private fun findDialog(supportFragmentManager: androidx.fragment.app.FragmentManager) =
        supportFragmentManager.findFragmentByTag(TAG_BOTTOM_SHEET) as MainDialogFragment?
}