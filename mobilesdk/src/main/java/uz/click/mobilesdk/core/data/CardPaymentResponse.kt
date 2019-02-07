package uz.click.mobilesdk.core.data

import com.squareup.moshi.Json
import java.io.Serializable

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
data class CardPaymentResponse(
    @field:Json(name = "error_code")
    val errorCode: Int,
    @field:Json(name = "error_note")
    val errorNote: String,
    @field:Json(name = "card_number")
    var cardNumber: String,
    @field:Json(name = "phone_number")
    var phoneNumber: String
) : Serializable