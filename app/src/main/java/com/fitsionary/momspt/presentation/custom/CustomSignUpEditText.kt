package com.fitsionary.momspt.presentation.custom

import android.content.Context
import android.graphics.Color
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.databinding.DataBindingUtil
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.CustomSignUpEditTextBinding

class CustomSignUpEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: CustomSignUpEditTextBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.custom_sign_up_edit_text, this, true
    )

    init {
        context.withStyledAttributes(attrs, R.styleable.SignUpEditText) {
            binding.etCustomSignUp.apply {
                filters =
                    arrayOf<InputFilter>(
                        LengthFilter(
                            getInt(
                                R.styleable.SignUpEditText_maxLen,
                                10
                            )
                        )
                    )
                inputType =
                    getInt(R.styleable.SignUpEditText_android_inputType, InputType.TYPE_CLASS_TEXT)
                setText(getString(R.styleable.SignUpEditText_inputText))
                hint = getString(R.styleable.SignUpEditText_hintText)
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    getDrawable(R.styleable.SignUpEditText_iconImg),
                    null, null, null
                )
            }
            binding.tvCustomSignUp.apply {
                text = getString(R.styleable.SignUpEditText_rightText)
                setTextColor(
                    getColor(
                        R.styleable.SignUpEditText_rightTextColor,
                        Color.BLACK
                    )
                )
            }
        }
    }

    fun setInputText(inputText: String) {
        binding.etCustomSignUp.setText(inputText)
    }

    fun setRightTextClickListener(clickListener: OnClickListener) {
        binding.tvCustomSignUp.setOnClickListener(clickListener)
    }
}