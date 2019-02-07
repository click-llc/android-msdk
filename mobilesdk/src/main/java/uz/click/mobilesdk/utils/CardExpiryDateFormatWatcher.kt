package uz.click.mobilesdk.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
abstract class CardExpiryDateFormatWatcher(val editText: EditText) : TextWatcher {
    companion object {
        const val EXPIRY_DATE_LENGTH = 4
    }

    var isTyped = true

    abstract fun afterTextWithoutPattern(expiredDate: String)

    override fun afterTextChanged(s: Editable?) {
        var cursorPosition = editText.selectionStart
        var text = editText.text.toString().replace("/", "")
        if (text.length >= EXPIRY_DATE_LENGTH) {
            text = text.substring(0, EXPIRY_DATE_LENGTH)
        }
        val textParsed = when {
            text.length >= 3 -> {
                text.substring(0, 2) + "/" + text.substring(2)
            }
            else -> text
        }
        if (isTyped) {
            when (cursorPosition) {
                3 -> cursorPosition++
            }
        } else {
            when (cursorPosition) {
                3 -> cursorPosition--
            }
        }
        if (text.isNotEmpty())
            editText.hint = ""
        else editText.hint = "09/20"
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