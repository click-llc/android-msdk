package uz.click.msdk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*
import uz.click.mobilesdk.core.ClickMerchant
import uz.click.mobilesdk.core.ClickMerchantConfig
import uz.click.mobilesdk.core.callbacks.ClickMerchantListener
import uz.click.mobilesdk.impl.paymentoptions.PaymentOptionEnum
import uz.click.mobilesdk.impl.paymentoptions.ThemeOptions

class MainActivity : AppCompatActivity() {

    private val productPrice = 1000.0
    private val productName = "Супер ТВ"
    private val productDescription = "Подписка на сервис Супер ТВ"

    //fake in-memory user
    private val currentUser = UserDetail(0, "", null, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        price.text = productPrice.toString()
        good_name.text = productName
        good_description.text = productDescription

        btnBuy.setOnClickListener {
            val config = ClickMerchantConfig.Builder()
                .serviceId(BuildConfig.SERVICE_ID)
                .merchantId(BuildConfig.MERCHANT_ID)
                .amount(productPrice)
                .locale("RU")
                .option(PaymentOptionEnum.USSD)
                .theme(ThemeOptions.NIGHT)
                .productName(productName)
                .productDescription(productDescription)
                .merchantUserId(BuildConfig.MERCHANT_USER_ID)
                .requestId(currentUser.requestId)
                .build()

            ClickMerchant.init(
                supportFragmentManager, config,
                object : ClickMerchantListener {
                    override fun onReceiveRequestId(id: String) {
                        currentUser.requestId = id
                    }

                    override fun onSuccess(paymentId: Long) {
                        currentUser.paymentId = paymentId
                        currentUser.paid = true
                    }

                    override fun onFailure() {
                        currentUser.requestId = ""
                    }

                    override fun onInvoiceCancelled() {
                        currentUser.requestId = ""
                    }
                }
            )
        }

    }
}