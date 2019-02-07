package uz.click.mobilesdk.core.data

import com.squareup.moshi.Json

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
data class InvoiceResponse(
    @field:Json(name = "error_code")
    val errorCode: Int,
    @field:Json(name = "error_note")
    val errorNote: String,
    @field:Json(name = "invoice_id")
    val invoiceId: String
)