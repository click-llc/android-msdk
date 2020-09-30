package uz.click.mobilesdk.impl.paymentoptions

import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_payment_option.*
import uz.click.mobilesdk.R
import uz.click.mobilesdk.core.errors.ArgumentEmptyException
import uz.click.mobilesdk.impl.MainDialogFragment
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.IS_CLICK_EVOLUTION_ENABLED
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.LOCALE
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.THEME_MODE
import uz.click.mobilesdk.utils.LanguageUtils
import java.util.*

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
class PaymentOptionListFragment : AppCompatDialogFragment() {
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
                    .inflate(R.layout.fragment_payment_option, container, false)
            }
            ThemeOptions.NIGHT -> {
                val contextWrapper = ContextThemeWrapper(activity, R.style.Theme_App_Dark)

                inflater.cloneInContext(contextWrapper)
                    .inflate(R.layout.fragment_payment_option, container, false)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = parentFragment as MainDialogFragment

        if (arguments != null) {
            val locale = Locale(arguments!!.getString(LOCALE, "ru"))
            tvTitle.text =
                LanguageUtils.getLocaleStringResource(locale, R.string.payment_types, context!!)
            val items = ArrayList<PaymentOption>()

            val isClickEvolutionEnabled = arguments!!.getBoolean(IS_CLICK_EVOLUTION_ENABLED, false)

            if (isClickEvolutionEnabled) {
                items.add(
                    PaymentOption(
                        R.drawable.ic_aevo,
                        LanguageUtils.getLocaleStringResource(
                            locale,
                            R.string.click_evo_app,
                            context!!
                        ),
                        LanguageUtils.getLocaleStringResource(
                            locale,
                            R.string.click_evo_app_description,
                            context!!
                        ),
                        PaymentOptionEnum.CLICK_EVOLUTION
                    )
                )
            }

            items.add(
                PaymentOption(
                    R.drawable.ic_880,
                    LanguageUtils.getLocaleStringResource(locale, R.string.invoicing, context!!),
                    LanguageUtils.getLocaleStringResource(
                        locale,
                        R.string.sms_confirmation,
                        context!!
                    ),
                    PaymentOptionEnum.USSD
                )
            )
            items.add(
                PaymentOption(
                    R.drawable.ic_cards,
                    LanguageUtils.getLocaleStringResource(locale, R.string.bank_card, context!!),
                    LanguageUtils.getLocaleStringResource(locale, R.string.card_props, context!!),
                    PaymentOptionEnum.BANK_CARD
                )
            )
            val adapter = PaymentOptionAdapter(context!!, themeMode, items)

            adapter.callback = object : PaymentOptionAdapter.OnPaymentOptionSelected {
                override fun selected(position: Int, item: PaymentOption) {
                    parent.paymentOptionSelected(item)
                }
            }

            rvPaymentTypes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            rvPaymentTypes.adapter = adapter
        } else throw ArgumentEmptyException()
    }
}