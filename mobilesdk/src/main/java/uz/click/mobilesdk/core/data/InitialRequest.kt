package uz.click.mobilesdk.core.data

import com.squareup.moshi.Json

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
data class InitialRequest(
    @field:Json(name = "service_id")
    var serviceId: Long,
    @field:Json(name = "merchant_id")
    var merchantId: Long,
    @field:Json(name = "amount")
    var amount: Double,
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
    var interfaceType: String?
)