package com.fitsionary.momspt.presentation.binding

import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.fitsionary.momspt.R
import com.fitsionary.momspt.util.*

@BindingAdapter("validationResultTextColor")
fun setValidationResultTextColor(textView: TextView, success: Boolean) {
    if (success) {
        textView.setTextColor(ResourcesCompat.getColor(textView.resources, R.color.blue, null))
    } else {
        textView.setTextColor(ResourcesCompat.getColor(textView.resources, R.color.pink, null))
    }
}

@BindingAdapter("rankText")
fun setRankText(textView: TextView, rank: String) {
    when (rank) {
        A_PLUS -> textView.setTextColor(
            ResourcesCompat.getColor(
                textView.resources,
                R.color.pink,
                null
            )
        )
        A -> textView.setTextColor(
            ResourcesCompat.getColor(
                textView.resources,
                R.color.pink_d44b62,
                null
            )
        )
        B -> textView.setTextColor(
            ResourcesCompat.getColor(
                textView.resources,
                R.color.blue_2b9bfd,
                null
            )
        )
        C -> textView.setTextColor(
            ResourcesCompat.getColor(
                textView.resources,
                R.color.orange_ec7653,
                null
            )
        )
        D -> textView.setTextColor(
            ResourcesCompat.getColor(
                textView.resources,
                R.color.dark_gray_818181,
                null
            )
        )
    }
    textView.text = rank
}

