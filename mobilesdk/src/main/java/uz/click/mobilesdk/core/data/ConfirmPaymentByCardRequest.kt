package uz.click.mobilesdk.core.data

import com.squareup.moshi.Json

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
data class ConfirmPaymentByCardRequest(
    @field:Json(name = "request_id")
    var requestId: String,
    @field:Json(name = "confirm_code")
    var confirmCode: String
)