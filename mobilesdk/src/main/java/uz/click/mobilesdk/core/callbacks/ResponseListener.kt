package uz.click.mobilesdk.core.callbacks

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
interface ResponseListener<T> {
    fun onFailure(e: Exception)
    fun onSuccess(response: T)
}