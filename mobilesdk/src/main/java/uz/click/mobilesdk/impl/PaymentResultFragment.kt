package uz.click.mobilesdk.impl

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_payment_result.*
import uz.click.mobilesdk.R
import uz.click.mobilesdk.core.callbacks.ClickMerchantListener
import uz.click.mobilesdk.core.data.PaymentResponse
import uz.click.mobilesdk.core.errors.ArgumentEmptyException
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.LOCALE
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.PAYMENT_AMOUNT
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.PAYMENT_RESULT
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.THEME_MODE
import uz.click.mobilesdk.impl.paymentoptions.ThemeOptions
import uz.click.mobilesdk.utils.LanguageUtils
import uz.click.mobilesdk.utils.formatDecimals
import uz.click.mobilesdk.utils.hide
import uz.click.mobilesdk.utils.show
import java.util.*

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
class PaymentResultFragment : AppCompatDialogFragment() {

    private var listener: ClickMerchantListener? = null
    private lateinit var locale: Locale
    private lateinit var themeMode: ThemeOptions

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
                    .inflate(R.layout.fragment_payment_result, container, false)
            }
            ThemeOptions.NIGHT -> {
                val contextWrapper = ContextThemeWrapper(activity, R.style.Theme_App_Dark)

                inflater.cloneInContext(contextWrapper)
                    .inflate(R.layout.fragment_payment_result, container, false)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (themeMode) {
            ThemeOptions.LIGHT -> {
                btnClose.setBackgroundResource(R.drawable.next_button_rounded)

            }
            ThemeOptions.NIGHT -> {
                btnClose.setBackgroundResource(R.drawable.next_button_rounded_dark)
            }
        }

        listener = (parentFragment as MainDialogFragment?)?.getListener()
        if (arguments != null) {
            val result = arguments!!.getSerializable(PAYMENT_RESULT) as PaymentResponse
            locale = Locale(arguments!!.getString(LOCALE, "ru"))
            tvPaymentTitle.text = result.paymentStatusDescription
            tvPaymentAmount.text = arguments!!.getDouble(PAYMENT_AMOUNT).formatDecimals()

            tvPaymentNumberTitle.text =
                LanguageUtils.getLocaleStringResource(locale, R.string.payment_number, context!!)
            tvPaid.text =
                LanguageUtils.getLocaleStringResource(locale, R.string.to_be_paid, context!!)
            tvAbbr.text = LanguageUtils.getLocaleStringResource(locale, R.string.abbr, context!!)
            tvClose.text = LanguageUtils.getLocaleStringResource(locale, R.string.close, context!!)

            if (result.paymentId != null) {
                llPaymentNumber.show()
                tvPaymentNumber.text = "${result.paymentId}"
            } else llPaymentNumber.hide()

            when {
                result.paymentStatus < 0 -> {
                    listener?.onFailure()
                    ivPaymentStatus.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_failure
                        )
                    )
                    tvPaymentSubtitle.text =
                        LanguageUtils.getLocaleStringResource(
                            locale,
                            R.string.failure_payment,
                            context!!
                        )
                    tvPaymentTitle.text =
                        LanguageUtils.getLocaleStringResource(
                            locale,
                            R.string.payment_failed,
                            context!!
                        )
                }
                result.paymentStatus == 2 -> {
                    listener?.onSuccess(result.paymentId!!)
                    ivPaymentStatus.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.ic_success
                        )
                    )
                    tvPaymentTitle.text =
                        LanguageUtils.getLocaleStringResource(
                            locale,
                            R.string.payment_success,
                            context!!
                        )
                    tvPaymentSubtitle.text =
                        LanguageUtils.getLocaleStringResource(
                            locale,
                            R.string.success_payment,
                            context!!
                        )
                }
            }
        } else throw ArgumentEmptyException()

        btnClose.setOnClickListener {
            parentFragment?.let {
                val parent = parentFragment as MainDialogFragment
                parent.close()
            }
        }
    }
}