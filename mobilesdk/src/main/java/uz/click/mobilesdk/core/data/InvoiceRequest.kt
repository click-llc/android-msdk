package uz.click.mobilesdk.core.data

import com.squareup.moshi.Json

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
data class InvoiceRequest(
    @field:Json(name = "request_id")
    var requestId: String,
    @field:Json(name = "phone_number")
    var phoneNumber: String
)