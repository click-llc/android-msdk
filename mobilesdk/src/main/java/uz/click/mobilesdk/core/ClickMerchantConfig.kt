package uz.click.mobilesdk.core

import uz.click.mobilesdk.core.errors.*
import uz.click.mobilesdk.impl.paymentoptions.PaymentOptionEnum
import java.io.Serializable

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
data class ClickMerchantConfig(
    var serviceId: Long,
    var merchantId: Long,
    var merchantUserId: Long,
    var requestId: String = "",
    var amount: Double,
    var transactionParam: String?,
    var communalParam: String?,
    var productName: String,
    var productDescription: String,
    var locale: String,
    var paymentOption: PaymentOptionEnum = PaymentOptionEnum.USSD
) : Serializable {
    class Builder {
        private var serviceId: Long? = null
        private var merchantId: Long? = null
        private var merchantUserId: Long? = null
        private var amount: Double? = null
        private var transactionParam: String = ""
        private var communalParam: String = ""
        private var locale: String? = null
        private var productName: String = ""
        private var productDescription: String = ""
        private var requestId: String = ""
        private var option: PaymentOptionEnum = PaymentOptionEnum.USSD

        fun amount(amount: Double) = apply { this.amount = amount }
        fun serviceId(serviceId: Long) = apply { this.serviceId = serviceId }
        fun merchantId(merchantId: Long) = apply { this.merchantId = merchantId }
        fun merchantUserId(merchantUserId: Long) = apply { this.merchantUserId = merchantUserId }
        fun requestId(requestId: String) = apply { this.requestId = requestId }
        fun transactionParam(transactionParam: String) = apply { this.transactionParam = transactionParam }
        fun communalParam(communalParam: String) = apply { this.communalParam = communalParam }
        fun productName(productName: String) = apply { this.productName = productName }
        fun productDescription(productDescription: String) = apply { this.productDescription = productDescription }
        fun locale(locale: String) = apply { this.locale = locale }
        fun option(option: PaymentOptionEnum) = apply { this.option = option }
        fun build(): ClickMerchantConfig {
            return ClickMerchantConfig(
                serviceId ?: throw ServiceIdEmptyException(),
                merchantId ?: throw MerchantIdEmptyException(),
                merchantUserId ?: throw MerchantUserIdEmptyException(),
                requestId,
                amount ?: throw AmountEmptyException(),
                transactionParam,
                communalParam,
                productName,
                productDescription,
                locale ?: throw LocaleEmptyException(),
                option
            )
        }
    }
}