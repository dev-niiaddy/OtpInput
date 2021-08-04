package com.godwinaddy.otpinput

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.card.MaterialCardView


open class OtpInput : LinearLayout {

    internal enum class OtpInputType(val type: Int) {
        NUMBER(1),
        NUMBER_HIDDEN(2)
    }

    private val mListOfEditables = mutableListOf<EditText>()
    private var mInputCount = 4

    private var mInputBackground: Int = 0
    private var mInputSpacing = 15
    private var mInputRadius = 20
    private var mTextColor: Int = 0
    private var mTextSize: Int = 0
    private var mTextStyle: Int = 0
    private var mInputType: Int = 1

    private var mBorderColor: Int = -1
    private var mBorderWidth: Int = 0
    private var mCursorVisibility = true
    private var mHintText = ""
    private var mHintColor: Int = -1

    val otpText: String
        get() {
            return mListOfEditables.joinToString(separator = "") {
                it.text.toString()
            }
        }

    var isCursorVisible: Boolean
        set(value) {
            mListOfEditables.forEach {
                it.isCursorVisible = value
            }
        }
        get() {
            val sum = mListOfEditables.sumBy {
                if (it.isCursorVisible) 1 else 0
            }

            return sum == mListOfEditables.size
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

        if (mInputCount <= 0) {
            throw RuntimeException("Input count cannot be $mInputCount. Use one or greater")
        }


        for (i in 1..mInputCount) {
            val inputView = inflate(context, R.layout.input_edit_text, null) as MaterialCardView
            inputView.radius = mInputRadius.toFloat()

            val editText = inputView.getChildAt(0) as EditText
            editText.setBackgroundColor(mInputBackground)
            editText.textSize = mTextSize.toFloat()
            editText.setTextColor(mTextColor)
            editText.setTypeface(editText.typeface, mTextStyle)
            editText.hint = mHintText.getOrNull(i - 1)?.toString() ?: ""

            when (mInputType) {
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
            editText.setOnKeyListener { _, keyCode, event ->

                if (keyCode == KeyEvent.KEYCODE_DEL
                    && event.action == KeyEvent.ACTION_UP
                    && editText.textString().isEmpty()
                ) {
                    moveBackward(editText)
                    return@setOnKeyListener true
                }

                return@setOnKeyListener false
            }

            if(mBorderColor!= -1) {
                inputView.strokeColor = mBorderColor
                inputView.strokeWidth = mBorderWidth
            }

            if(mHintColor != -1) {
                editText.setHintTextColor(mHintColor)
            }

            mListOfEditables.add(editText)

            this.addView(inputView)
        }

        isCursorVisible = mCursorVisibility
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.OtpInput, 0, 0)

        mInputCount = typedArray.getInt(R.styleable.OtpInput_inputCount, 4)
        mInputBackground = typedArray.getColor(
            R.styleable.OtpInput_inputBackground,
            ContextCompat.getColor(context, android.R.color.transparent)
        )

        mInputSpacing = typedArray.getDimensionPixelSize(
            R.styleable.OtpInput_inputSpacing,
            20,
        )

        val defaultRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f, resources.displayMetrics
        ).toInt()

        mInputRadius = typedArray.getDimensionPixelSize(
            R.styleable.OtpInput_inputRadius,
            defaultRadius
        )

        mTextColor = typedArray.getColor(
            R.styleable.OtpInput_android_textColor,
            ContextCompat.getColor(context, android.R.color.black)
        )

        val defaultTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            12f, resources.displayMetrics
        ).toInt()

        mTextSize = typedArray.getDimensionPixelSize(
            R.styleable.OtpInput_android_textSize,
            defaultTextSize
        )

        mTextStyle = typedArray.getInt(
            R.styleable.OtpInput_android_textStyle,
            0
        )

        mInputType = typedArray.getInt(
            R.styleable.OtpInput_inputType,
            OtpInputType.NUMBER.type
        )

        mBorderColor = typedArray.getColor(
            R.styleable.OtpInput_borderColor,
            -1
        )

        mBorderWidth = typedArray.getDimensionPixelSize(
            R.styleable.OtpInput_borderWidth,
            0
        )

        mHintText = typedArray.getString(
            R.styleable.OtpInput_android_hint
        ) ?: ""

        typedArray.getColor(
            R.styleable.OtpInput_android_textColorHint,
            -1
        )

        mCursorVisibility = typedArray.getBoolean(
            R.styleable.OtpInput_android_cursorVisible,
            true
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
            params.width = ((viewWidth - ((mInputCount - 1) * mInputSpacing)) / mInputCount)
            params.height = ViewGroup.LayoutParams.MATCH_PARENT

            if (view != lastChild) {
                params.setMargins(0, 0, mInputSpacing, 0)
            }
        }
    }

    private fun moveToNext(editText: EditText) {
        val currentInput = mListOfEditables.indexOf(editText) + 1

        if (currentInput < mListOfEditables.size) {
            val v = mListOfEditables[currentInput]
            v.demandFocus()
        }
    }

    private fun moveBackward(editText: EditText) {
        val currentInput = mListOfEditables.indexOf(editText) - 1

        if (currentInput >= 0) {
            val v = mListOfEditables[currentInput]
            v.demandFocus()
        }
    }

    private fun EditText.demandFocus() {
        this.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun focusOtpInput() {
        val firstInput = mListOfEditables.first()

        firstInput.postDelayed({
            firstInput.clearFocus()
            firstInput.demandFocus()
        }, 100)
    }

    /*Takes a function which accepts the {inputComplete} boolean and current {otpText}*/
    fun inputChangedListener(onChanged: (inputComplete: Boolean, otpText: String) -> Unit) {
        mListOfEditables.forEach {
            it.addTextChangedListener(object : TextWatcherAdapter() {
                override fun afterTextChanged(s: Editable) {
                    onChanged(otpText.length == mInputCount, otpText)
                }
            })
        }
    }

    fun onInputFinishedListener(onInputFinished: (String) -> Unit) {
        mListOfEditables.last().addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                if (otpText.length == mInputCount) {
                    onInputFinished(otpText)
                }
            }
        })
    }

    fun reset() {
        mListOfEditables.forEach {
            it.setText("")
        }

        mListOfEditables.firstOrNull()?.requestFocus()
    }

    fun setHint(hintText: String) {
        mListOfEditables.forEachIndexed { index, editText ->
            editText.hint = hintText.getOrNull(index)?.toString() ?: ""
        }
    }

    fun setStrokeColor(@ColorInt color: Int) {
        mListOfEditables.forEach { editText ->
            val parent = editText.parent
            if(parent is MaterialCardView) {
                parent.strokeColor = color
            }
        }
    }

    fun setStrokeWidth(width: Int) {
        mListOfEditables.forEach { editText ->
            val parent = editText.parent
            if(parent is MaterialCardView) {
                parent.strokeWidth = width
            }
        }
    }
}