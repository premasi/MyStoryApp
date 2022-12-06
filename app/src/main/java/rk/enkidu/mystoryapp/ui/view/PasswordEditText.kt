package rk.enkidu.mystoryapp.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import rk.enkidu.mystoryapp.R

class PasswordEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var showButtonText: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet){
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleInt: Int) : super(context, attributeSet, defStyleInt){
        init()
    }

    private fun init() {
        showButtonText = ContextCompat.getDrawable(context, R.drawable.ic_baseline_remove_red_eye_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()) showButton() else hideButton()
            }

            override fun afterTextChanged(p0: Editable?) {
                //do nothing
            }

        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setSelection(editableText.length)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START

        if(editableText.isNotEmpty() && editableText.length < 6) {
            error = context.getString(R.string.error_message_password)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val buttonStart: Float
            val buttonEnd: Float
            var isButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                buttonEnd = (showButtonText.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < buttonEnd -> isButtonClicked = true
                }
            } else {
                buttonStart = (width - paddingEnd - showButtonText.intrinsicWidth).toFloat()
                when {
                    event.x > buttonStart -> isButtonClicked = true
                }
            }
            if (isButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        showButtonText = ContextCompat.getDrawable(context, R.drawable.ic_baseline_remove_red_eye_24) as Drawable
                        showButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        showButtonText = ContextCompat.getDrawable(context, R.drawable.ic_baseline_remove_red_eye_24) as Drawable
                        when {
                            text != null -> {
                                transformationMethod = if(transformationMethod == PasswordTransformationMethod.getInstance()){
                                    HideReturnsTransformationMethod.getInstance()
                                } else {
                                    PasswordTransformationMethod.getInstance()
                                }

                            }
                        }
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }

    private fun showButton(){
        setButtonDrawbles(endOfTheText = showButtonText)
    }

    private fun hideButton(){
        setButtonDrawbles()
    }

    private fun setButtonDrawbles(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}