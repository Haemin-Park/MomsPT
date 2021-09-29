package com.fitsionary.momspt.presentation.binding

import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.fitsionary.momspt.R

@BindingAdapter("validationResultTextColor")
fun setValidationResultTextColor(textView: TextView, success: Boolean) {
    if (success) {
        textView.setTextColor(ResourcesCompat.getColor(textView.resources, R.color.blue, null))
    } else {
        textView.setTextColor(ResourcesCompat.getColor(textView.resources, R.color.pink, null))
    }
}