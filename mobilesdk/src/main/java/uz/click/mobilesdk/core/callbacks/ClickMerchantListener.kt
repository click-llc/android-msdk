package uz.click.mobilesdk.core.callbacks

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
interface ClickMerchantListener {
    fun onReceiveRequestId(id: String)
    fun onSuccess(paymentId: Long)
    fun onFailure()
    fun onInvoiceCancelled()
}