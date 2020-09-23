package com.godwinaddy.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
    }

}