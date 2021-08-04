package com.godwinaddy.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.godwinaddy.otpinput.OtpInput

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val otpInput = findViewById<OtpInput>(R.id.otpInput)
        otpInput.onInputFinishedListener {
            Log.d("Input Finished", it)
        }

        otpInput.focusOtpInput()
        otpInput.setHint("!#$%@")
        otpInput.setStrokeWidth(50)
        otpInput.setStrokeColor(ContextCompat.getColor(this, R.color.colorAccent))
        otpInput.isCursorVisible = false
    }

}