package com.godwinaddy.otpinput

import android.widget.EditText

fun EditText.textString(): String {
    return this.text?.toString() ?: ""
}