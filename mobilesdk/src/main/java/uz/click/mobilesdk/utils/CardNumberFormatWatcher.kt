package uz.click.mobilesdk.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
abstract class CardNumberFormatWatcher(val editText: EditText) : TextWatcher {
    var isTyped = true

    companion object {
        const val CARD_NUMBER_LENGTH = 16
    }

    abstract fun afterTextWithoutPattern(cardNumber: String)

    override fun afterTextChanged(s: Editable) {
        var cursorPosition = editText.selectionStart
        var text = editText.text.toString().replace(" ", "")
        if (text.length >= CARD_NUMBER_LENGTH) {
            text = text.substring(0, 16)
        }
        val textParsed = when {
            text.length >= 12 -> {
                text.substring(0, 4) + " " + text.substring(4, 8) +
                        " " + text.subSequence(8, 12) + " " + text.substring(12)
            }
            text.length >= 8 -> {
                text.substring(0, 4) + " " + text.substring(4, 8) + " " + text.substring(8)
            }
            text.length >= 4 -> {
                text.substring(0, 4) + " " + text.substring(4)
            }
            else -> text
        }
        if (isTyped) {
            when (cursorPosition) {
                5 -> cursorPosition++
                10 -> cursorPosition++
                15 -> cursorPosition++
            }
        } else {
            when (cursorPosition) {
                5 -> cursorPosition--
                10 -> cursorPosition--
                15 -> cursorPosition--
            }
        }

        if (cursorPosition > textParsed.length) cursorPosition = textParsed.length
        editText.removeTextChangedListener(this)
        editText.setText(textParsed)
        afterTextWithoutPattern(text)
        editText.setSelection(cursorPosition)
        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        isTyped = when {
            count > 0 -> false
            after > 0 -> true
            else -> true
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}