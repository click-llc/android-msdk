package uz.click.mobilesdk.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PhoneNumberTextWatcher(val editText: EditText): TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        var cursorPosition = editText.selectionStart
        var text = editText.text.toString().replace(" ", "")
        if (text.length >= 9) {
            text = text.substring(0, 9)
        }
        val textParsed = when {
            text.length >= 7 -> {
                text.substring(0, 2) + " " + text.substring(2, 5) + " " + text.subSequence(5, 7) + " " + text.substring(7)
            }
            text.length >= 5 -> {
                text.substring(0, 2) + " " + text.substring(2, 5) + " " + text.substring(5)
            }
            text.length >= 2 -> {
                text.substring(0, 2) + " " + text.substring(2)
            }
            else -> text
        }
        when (cursorPosition) {
            3 -> cursorPosition++
            7 -> cursorPosition++
            10 -> cursorPosition++
        }
        if (cursorPosition > textParsed.length) cursorPosition = textParsed.length
        editText.removeTextChangedListener(this)
        editText.setText(textParsed)
        editText.setSelection(cursorPosition)
        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}