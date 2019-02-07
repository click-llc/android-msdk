package uz.click.mobilesdk.utils

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
object ValidationUtils {

    fun isCardValid(str: String): Boolean {
        if (str.isEmpty()) {
            return false
        }
        if (str.length != 16 && str.length != 19) {
            return false
        }
        if (str.length == 19 && (!Character.isSpaceChar(str[4]) || !Character.isSpaceChar(str[9]) || !Character.isSpaceChar(
                str[14]
            ))
        ) {
            return false
        }
        if (str.length == 16 && str.contains(' ')) {
            return false
        }
        for (i in 0 until str.length) {

            if (str.length == 16) {
                if (!Character.isDigit(str[i])) {
                    return false
                }
            }

            if (str.length == 19) {
                if (!Character.isDigit(str[i])) {
                    if (!Character.isSpaceChar(str[i]))
                        return false
                }
            }
        }
        return true
    }

    fun isExpireDateValid(str: String): Boolean {
        return str.isNotEmpty() && str.length == 5
                && Character.isDigit(str[0])
                && Character.isDigit(str[1])
                && Character.isDigit(str[3])
                && Character.isDigit(str[4])
    }

}