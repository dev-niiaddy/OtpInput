package com.godwinaddy.otpinput

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.children


open class OtpInput : LinearLayout {

    internal enum class OtpInputType(val type: Int) {
        NUMBER(1),
        NUMBER_HIDDEN(2)
    }

    private val listOfEditables = mutableListOf<EditText>()
    private var inputCount = 4

    private var inputBackground: Int = 0
    private var inputSpacing = 15
    private var inputRadius = 20
    private var textColor: Int = 0
    private var textSize: Int = 0
    private var textStyle: Int = 0
    private var inputType: Int = 1

    val otpText: String
        get() {
            return listOfEditables.joinToString(separator = "") {
                it.text.toString()
            }
        }


    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }


    private fun init(context: Context, attrs: AttributeSet?) {
        inflate(context, R.layout.otp_input, this)

        if (attrs != null) {
            initAttrs(attrs)
        }

        if (inputCount <= 0) {
            throw RuntimeException("Input count cannot be $inputCount. Use one or greater")
        }


        for (i in 1..inputCount) {
            val inputView = inflate(context, R.layout.input_edit_text, null) as CardView
            inputView.radius = inputRadius.toFloat()

            val editText = inputView.getChildAt(0) as EditText
            editText.setBackgroundColor(inputBackground)
            editText.textSize = textSize.toFloat()
            editText.setTextColor(textColor)
            editText.setTypeface(editText.typeface, textStyle)

            when (inputType) {
                OtpInputType.NUMBER.type -> {
                    editText.inputType =
                        InputType.TYPE_CLASS_NUMBER
                }
                OtpInputType.NUMBER_HIDDEN.type -> {
                    editText.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                }
            }
            editText.addTextChangedListener(object : TextWatcherAdapter() {

                override fun afterTextChanged(s: Editable) {
                    if (s.isNotEmpty()) {
                        moveToNext(editText)
                    } else {
                        moveBackward(editText)
                    }
                }
            })

            listOfEditables.add(editText)

            this.addView(inputView)
        }
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.OtpInput, 0, 0)

        inputCount = typedArray.getInt(R.styleable.OtpInput_inputCount, 4)
        inputBackground =
            typedArray.getColor(
                R.styleable.OtpInput_inputBackground,
                ContextCompat.getColor(context, android.R.color.transparent)
            )

        inputSpacing = typedArray.getDimensionPixelSize(R.styleable.OtpInput_inputSpacing, 20)

        val defaultRadius = TypedValue
            .applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f, resources.displayMetrics
            ).toInt()

        inputRadius = typedArray.getDimensionPixelSize(
            R.styleable.OtpInput_inputRadius,
            defaultRadius
        )

        textColor = typedArray.getColor(
            R.styleable.OtpInput_android_textColor,
            ContextCompat.getColor(context, android.R.color.black)
        )

        val defaultTextSize = TypedValue
            .applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                12f, resources.displayMetrics
            ).toInt()

        textSize = typedArray.getDimensionPixelSize(
            R.styleable.OtpInput_android_textSize,
            defaultTextSize
        )

        textStyle = typedArray.getInt(
            R.styleable.OtpInput_android_textStyle,
            0
        )

        inputType = typedArray.getInt(
            R.styleable.OtpInput_inputType,
            OtpInputType.NUMBER.type
        )

        typedArray.recycle()
    }

    /*
    * measure and resize input card views based on spacing and layout width*/
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        val viewChildren = children.toList()
        val lastChild = viewChildren.last()

        viewChildren.forEach {
            val view = it as CardView

            val params = view.layoutParams as LayoutParams
            params.width = ((viewWidth - ((inputCount - 1) * inputSpacing)) / inputCount)
            params.height = ViewGroup.LayoutParams.MATCH_PARENT

            if (view != lastChild) {
                params.setMargins(0, 0, inputSpacing, 0)
            }
        }
    }

    private fun moveToNext(editText: EditText) {
        val currentInput = listOfEditables.indexOf(editText) + 1

        if (currentInput < listOfEditables.size) {
            val v = listOfEditables[currentInput]
            v.demandFocus()
        }
    }

    private fun moveBackward(editText: EditText) {
        val currentInput = listOfEditables.indexOf(editText) - 1

        if (currentInput >= 0) {
            val v = listOfEditables[currentInput]
            v.demandFocus()
        }
    }

    private fun EditText.demandFocus() {
        this.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun focusOtpInput() {
        val firstInput = listOfEditables.first()

        firstInput.postDelayed({
            firstInput.clearFocus()
            firstInput.demandFocus()
        }, 100)
    }

    /*Takes a function which accepts the {inputComplete} boolean and current {otpText}*/
    fun inputChangedListener(onChanged: (inputComplete: Boolean, otpText: String) -> Unit) {
        listOfEditables.forEach {
            it.addTextChangedListener(object : TextWatcherAdapter() {
                override fun afterTextChanged(s: Editable) {
                        onChanged(otpText.length == inputCount, otpText)
                }
            })
        }
    }

    fun onInputFinishedListener(onInputFinished: (String) -> Unit) {
        listOfEditables.last().addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                if (otpText.length == inputCount) {
                    onInputFinished(otpText)
                }
            }
        })
    }

    fun reset() {
        listOfEditables.forEach {
            it.setText("")
        }
    }
}