package com.fitsionary.momspt.presentation.custom

import android.content.Context
import android.graphics.Color
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

    init {
        val binding: CustomSignUpEditTextBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.custom_sign_up_edit_text, this, true
        )

        context.withStyledAttributes(attrs, R.styleable.SignUpEditText) {
            binding.etCustomSignUp.hint = getString(R.styleable.SignUpEditText_hintText)
            binding.tvCustomSignUp.text = getString(R.styleable.SignUpEditText_rightText)
            binding.tvCustomSignUp.setTextColor(
                getColor(
                    R.styleable.SignUpEditText_rightTextColor,
                    Color.BLACK
                )
            )
            binding.etCustomSignUp.setCompoundDrawablesRelativeWithIntrinsicBounds(
                getDrawable(R.styleable.SignUpEditText_iconImg),
                null, null, null
            )
        }
    }
}