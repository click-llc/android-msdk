package uz.click.mobilesdk.impl.paymentoptions

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
data class PaymentOption(
    val image: Int,
    val title: String,
    val subtitle: String,
    val type: PaymentOptionEnum
)