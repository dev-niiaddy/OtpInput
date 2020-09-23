package com.godwinaddy.otpinput

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val otpInput = findViewById<OtpInput>(R.id.otpInput)
        otpInput.onInputFinishedListener {
            Log.d("Input Finished", it)
        }

        otpInput.focusOtpInput()
    }

}