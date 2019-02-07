package uz.click.mobilesdk.impl.paymentoptions

import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_payment_option.*
import uz.click.mobilesdk.R
import uz.click.mobilesdk.core.errors.ArgumentEmptyException
import uz.click.mobilesdk.impl.MainDialogFragment
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.LOCALE
import uz.click.mobilesdk.utils.LanguageUtils
import java.util.*

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
class PaymentOptionListFragment : AppCompatDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_option, container, false)
    }
    init {
        setStyle(STYLE_NO_FRAME, R.style.cl_FullscreenDialogTheme)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = parentFragment as MainDialogFragment

        if (arguments != null) {
            val locale = Locale(arguments!!.getString(LOCALE, "ru"))
            tvTitle.text = LanguageUtils.getLocaleStringResource(locale, R.string.payment_types, context!!)
            val items = ArrayList<PaymentOption>()
            items.add(
                PaymentOption(
                    R.drawable.ic_880,
                    LanguageUtils.getLocaleStringResource(locale, R.string.invoicing, context!!),
                    LanguageUtils.getLocaleStringResource(locale, R.string.sms_confirmation, context!!),
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
            val adapter = PaymentOptionAdapter(context!!, items)

            adapter.callback = object : PaymentOptionAdapter.OnPaymentOptionSelected {
                override fun selected(position: Int, item: PaymentOption) {
                    parent.paymentOptionSelected(item)
                }
            }

            rvPaymentTypes.layoutManager = LinearLayoutManager(context)
            rvPaymentTypes.adapter = adapter
        } else throw ArgumentEmptyException()
    }
}