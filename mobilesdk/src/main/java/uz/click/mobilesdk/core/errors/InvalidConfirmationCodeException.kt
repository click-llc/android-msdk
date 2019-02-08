package uz.click.mobilesdk.core.errors

/**
 * @author rahmatkhujaevs on 30/01/19
 * */
class InvalidConfirmationCodeException(var code: Int?, var msg: String?) : Exception()