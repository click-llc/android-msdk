package uz.click.mobilesdk.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * @author rahmatkhujaevs on 29/01/19
 * */

const val DEFAULT_DECIMAL_FORMAT = "0.##"

fun Float.formatDecimals(format: String = DEFAULT_DECIMAL_FORMAT): String {
    val decimalFormat = DecimalFormat(format)
    decimalFormat.groupingSize = 3
    decimalFormat.maximumFractionDigits = 2
    val symbol = DecimalFormatSymbols()
    symbol.groupingSeparator = ','
    decimalFormat.decimalFormatSymbols = symbol
    return decimalFormat.format(this)
}

fun Double.formatDecimals(): String {
    val decimalFormat = DecimalFormat()
    decimalFormat.groupingSize = 3
    decimalFormat.maximumFractionDigits = 2
    val symbol = DecimalFormatSymbols()
    symbol.groupingSeparator = ','
    decimalFormat.decimalFormatSymbols = symbol
    return decimalFormat.format(this)
}