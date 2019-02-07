package uz.click.mobilesdk.core.data

import com.squareup.moshi.Json
import java.io.Serializable

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
data class PaymentResponse(
    @field:Json(name = "payment_status_description")
    var paymentStatusDescription: String?,
    @field:Json(name = "payment_id")
    var paymentId: Long?,
    @field:Json(name = "payment_status")
    var paymentStatus: Int,
    @field:Json(name = "is_invoice")
    var invoice: Int
) : Serializable