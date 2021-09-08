package com.fitsionary.momspt.presentation.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.databinding.DataBindingUtil
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.CustomSignInButtonBinding


class CustomSignInButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        val binding: CustomSignInButtonBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.custom_sign_in_button, this, true
        )

        context.withStyledAttributes(attrs, R.styleable.SignInButton) {
            binding.layoutCustomSignIn.backgroundTintList =
                ColorStateList.valueOf(
                    getColor(
                        R.styleable.SignInButton_bgColor,
                        Color.BLACK
                    )
                )
            binding.tvCustomSignIn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                getDrawable(R.styleable.SignInButton_symbolImg),
                null, null, null
            )
            binding.tvCustomSignIn.text = getString(R.styleable.SignInButton_text)
            binding.tvCustomSignIn.setTextColor(
                getColor(
                    R.styleable.SignInButton_textColor,
                    Color.WHITE
                )
            )
        }
    }
}