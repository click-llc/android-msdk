package uz.click.mobilesdk.core.data

import com.squareup.moshi.Json

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
data class CardPaymentRequest(
    @field:Json(name = "request_id")
    var requestId: String,
    @field:Json(name = "card_number")
    var cardNumber: String,
    @field:Json(name = "expire_date")
    var expireDate: String
)