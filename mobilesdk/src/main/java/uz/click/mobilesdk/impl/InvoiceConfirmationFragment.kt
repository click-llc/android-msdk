package uz.click.mobilesdk.impl

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_invoice_confirmation.*
import uz.click.mobilesdk.R
import uz.click.mobilesdk.core.ClickMerchantManager
import uz.click.mobilesdk.core.callbacks.ResponseListener
import uz.click.mobilesdk.core.data.CheckoutResponse
import uz.click.mobilesdk.core.errors.ArgumentEmptyException
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.THEME_MODE
import uz.click.mobilesdk.impl.paymentoptions.ThemeOptions
import uz.click.mobilesdk.utils.ContextUtils.isAppAvailable
import uz.click.mobilesdk.utils.LanguageUtils
import java.util.*

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
class InvoiceConfirmationFragment : AppCompatDialogFragment() {

    private var requestId = ""
    private val clickMerchantManager = ClickMerchantManager()
    lateinit var themeMode: ThemeOptions

    companion object {
        private const val APP_NAME = "air.com.ssdsoftwaresolutions.clickuz"
        private const val TELEGRAM_BOT_NAME = "http://telegram.me/clickuz"
        private const val PLAY_STORE_ADDRESS = "http://play.google.com/store/apps/details?id="
        private const val CLICK_USSD = "*880#"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments == null) throw ArgumentEmptyException()

        themeMode = arguments!!.getSerializable(THEME_MODE) as ThemeOptions
        when (themeMode) {
            ThemeOptions.LIGHT -> {
                setStyle(STYLE_NO_FRAME, R.style.cl_FullscreenDialogTheme)
            }
            ThemeOptions.NIGHT -> {
                setStyle(STYLE_NO_FRAME, R.style.cl_FullscreenDialogThemeDark)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return when (themeMode) {
            ThemeOptions.LIGHT -> {
                val contextWrapper = ContextThemeWrapper(activity, R.style.Theme_App_Light)

                inflater.cloneInContext(contextWrapper)
                    .inflate(R.layout.fragment_invoice_confirmation, container, false)
            }
            ThemeOptions.NIGHT -> {
                val contextWrapper = ContextThemeWrapper(activity, R.style.Theme_App_Dark)

                inflater.cloneInContext(contextWrapper)
                    .inflate(R.layout.fragment_invoice_confirmation, container, false)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener = (parentFragment as MainDialogFragment?)?.getListener()
        when (themeMode) {
            ThemeOptions.LIGHT -> {
                btnBack.setBackgroundResource(R.drawable.next_button_rounded)
            }
            ThemeOptions.NIGHT -> {
                btnBack.setBackgroundResource(R.drawable.next_button_rounded_dark)
            }
        }

        if (arguments != null) {
            requestId = arguments!!.getString(MainDialogFragment.REQUEST_ID, "")
            val locale = Locale(arguments!!.getString(MainDialogFragment.LOCALE, "ru"))
            tvTitle.text = LanguageUtils.getLocaleStringResource(
                locale,
                R.string.waiting_confirmation,
                context!!
            )
            tvSubtitle.text =
                LanguageUtils.getLocaleStringResource(locale, R.string.invoice_placed, context!!)
            tvConfirmMethods.text =
                LanguageUtils.getLocaleStringResource(locale, R.string.ways_to_confirm, context!!)
            tvBack.text = LanguageUtils.getLocaleStringResource(locale, R.string.cancel, context!!)
            tvBotInfo.text = LanguageUtils.getLocaleStringResource(
                locale,
                R.string.click_bot_sent_code,
                context!!
            )
            tvUssdInfo.text =
                LanguageUtils.getLocaleStringResource(locale, R.string.call_ussd, context!!)
            tvClickApp.text =
                LanguageUtils.getLocaleStringResource(locale, R.string.click_app, context!!)
            tvClickAppInfo.text = LanguageUtils.getLocaleStringResource(
                locale,
                R.string.enter_invoices_list,
                context!!
            )
            checkConfirmation()
        } else throw ArgumentEmptyException()

        llUSSD.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${Uri.encode(CLICK_USSD)}")
            startActivity(intent)
        }

        llApp.setOnClickListener {
            if (isAppAvailable(context!!, APP_NAME)) {
                val intent = context?.packageManager?.getLaunchIntentForPackage(APP_NAME)
                startActivity(intent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$PLAY_STORE_ADDRESS$APP_NAME")))
            }
        }

        llTelegram.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TELEGRAM_BOT_NAME)))
        }

        btnBack.setOnClickListener {
            clickMerchantManager.invoiceCancelled = true
            listener?.onInvoiceCancelled()
            (parentFragment as MainDialogFragment?)?.close()
        }
    }

    private fun checkConfirmation() {
        progressIndicator.show()
        clickMerchantManager.checkPaymentByRequestIdContinuously(
            requestId,
            object : ResponseListener<CheckoutResponse> {
                override fun onFailure(e: Exception) {
                    e.printStackTrace()
                }

                override fun onSuccess(response: CheckoutResponse) {
                    parentFragment?.let {
                        val parent = it as MainDialogFragment
                        parent.openPaymentResultPage(response.payment)
                    }
                }

            })
    }
}