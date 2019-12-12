package uz.click.mobilesdk.impl

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.fragment_confirm_payment.*
import uz.click.mobilesdk.BuildConfig
import uz.click.mobilesdk.R
import uz.click.mobilesdk.core.ClickMerchantManager
import uz.click.mobilesdk.core.callbacks.ResponseListener
import uz.click.mobilesdk.core.data.CardPaymentResponse
import uz.click.mobilesdk.core.data.ConfirmPaymentByCardResponse
import uz.click.mobilesdk.core.data.PaymentResponse
import uz.click.mobilesdk.core.errors.ArgumentEmptyException
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.LOCALE
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.REQUEST_ID
import uz.click.mobilesdk.impl.MainDialogFragment.Companion.THEME_MODE
import uz.click.mobilesdk.impl.paymentoptions.ThemeOptions
import uz.click.mobilesdk.utils.ErrorUtils
import uz.click.mobilesdk.utils.LanguageUtils
import uz.click.mobilesdk.utils.hide
import uz.click.mobilesdk.utils.hideKeyboard
import uz.click.mobilesdk.utils.invisible
import uz.click.mobilesdk.utils.show
import java.lang.IllegalStateException
import java.util.*

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
class PaymentConfirmationFragment : AppCompatDialogFragment() {

    private var payment: CardPaymentResponse? = null
    private var requestId: String? = ""
    private lateinit var locale: Locale
    private val clickMerchantManager = ClickMerchantManager()
    lateinit var themeMode: ThemeOptions

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
                    .inflate(R.layout.fragment_confirm_payment, container, false)
            }
            ThemeOptions.NIGHT -> {
                val contextWrapper = ContextThemeWrapper(activity, R.style.Theme_App_Dark)

                inflater.cloneInContext(contextWrapper)
                    .inflate(R.layout.fragment_confirm_payment, container, false)
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (themeMode) {
            ThemeOptions.LIGHT -> {
                btnNext.setBackgroundResource(R.drawable.next_button_rounded)
                viewMobileNumberUnderline.setBackgroundResource(R.drawable.underline_background)
            }
            ThemeOptions.NIGHT -> {
                btnNext.setBackgroundResource(R.drawable.next_button_rounded_dark)
                viewMobileNumberUnderline.setBackgroundResource(R.drawable.underline_background_dark)

            }
        }
        if (arguments != null) {
            payment =
                arguments!!.getSerializable(MainDialogFragment.PAYMENT_RESULT) as CardPaymentResponse?
            requestId = arguments!!.getString(REQUEST_ID)
            locale = Locale(arguments!!.getString(LOCALE, "ru"))
            tvTitle.text =
                LanguageUtils.getLocaleStringResource(locale, R.string.confirm_with_sms, context!!)
            tvSubtitle.text =
                LanguageUtils.getLocaleStringResource(locale, R.string.sms_code_sent, context!!)
            tvNext.text = LanguageUtils.getLocaleStringResource(locale, R.string.next, context!!)
            tvMobileNumberOwner.text =
                LanguageUtils.getLocaleStringResource(
                    locale,
                    R.string.owner_mobile_number,
                    context!!
                )

            etCode.hint =
                LanguageUtils.getLocaleStringResource(locale, R.string.sms_code, context!!)

            tvMobileNumber.text = if (payment != null) "+" + payment?.phoneNumber else {
                val prefs =
                    context?.getSharedPreferences(BuildConfig.BASE_XML, Context.MODE_PRIVATE)
                prefs?.getString(requestId, "")
            }
        } else throw ArgumentEmptyException()

        etCode.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                confirm()
                etCode.hideKeyboard()
            }
            true
        }

        btnNext.setOnClickListener {
            confirm()
            btnNext.hideKeyboard()
        }

        etCode.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            viewMobileNumberUnderline.isEnabled = !hasFocus
        }
    }

    private fun confirm() {
        if (etCode.text.toString().isEmpty()) {
            etCode.error =
                LanguageUtils.getLocaleStringResource(locale, R.string.enter_code, context!!)
        } else {
            showLoading()
            clickMerchantManager.confirmPaymentByCard(
                requestId!!,
                etCode.text.toString(),
                object : ResponseListener<ConfirmPaymentByCardResponse> {
                    override fun onFailure(e: Exception) {
                        e.printStackTrace()
                        showError()
                        showErrorMessage(e)
                    }

                    override fun onSuccess(response: ConfirmPaymentByCardResponse) {
                        hideLoading()
                        (parentFragment as MainDialogFragment).openPaymentResultPage(
                            PaymentResponse(
                                response.paymentStatusNote,
                                response.paymentId,
                                response.paymentStatus ?: throw IllegalStateException(),
                                0
                            )
                        )
                    }
                })
        }
    }

    private fun showError() {
        activity?.runOnUiThread {
            pbLoading.hide()
            llContainer.show()
            tvError.show()
        }
    }

    private fun showErrorMessage(e: Exception) {
        e.printStackTrace()
        if (ErrorUtils.isApiError(e)) {
            activity?.runOnUiThread {
                tvError.show()
                tvError.text = ErrorUtils.getErrorMessage(
                    e,
                    locale,
                    context!!
                )
            }
        } else activity?.runOnUiThread {
            tvError.show()
            tvError.text = LanguageUtils.getLocaleStringResource(
                locale,
                R.string.network_connection_error,
                context!!
            )
        }
    }

    private fun showLoading() {
        pbLoading.show()
        llContainer.invisible()
        tvError.hide()
    }

    private fun hideLoading() {
        activity?.runOnUiThread {
            pbLoading.hide()
            llContainer.show()
            tvError.hide()
        }
    }
}