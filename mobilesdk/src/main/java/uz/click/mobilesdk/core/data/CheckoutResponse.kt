package uz.click.mobilesdk.core.data

import com.squareup.moshi.Json

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
data class CheckoutResponse(
    @field:Json(name = "request_id")
    var requestId: String,
    @field:Json(name = "service_id")
    var serviceId: Long,
    @field:Json(name = "merchant_id")
    var merchantId: Long,
    @field:Json(name = "service_name")
    var serviceName: String,
    @field:Json(name = "amount")
    var amount: Double,
    @field:Json(name = "commission_percent")
    var commissionPercent: Double?,
    @field:Json(name = "transaction_param")
    var transactionParam: String?,
    @field:Json(name = "communal_param")
    var communalParam: String?,
    @field:Json(name = "return_url")
    var returnUrl: String?,
    @field:Json(name = "merchant_user_id")
    var merchantUserId: Long,
    @field:Json(name = "language")
    var language: String,
    @field:Json(name = "interface_type")
    var interfaceType: String?,
    @field:Json(name = "image_url")
    var imageUrl: String?,
    @field:Json(name = "payment")
    var payment: PaymentResponse
)