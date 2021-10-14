package com.fitsionary.momspt.presentation.binding

import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.enum.RankEnum

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
        RankEnum.A_PLUS.name -> {
            textView.setTextColor(
                ResourcesCompat.getColor(
                    textView.resources,
                    R.color.pink,
                    null
                )
            )
            textView.text = RankEnum.A_PLUS.rankName
        }
        RankEnum.A.name -> {
            textView.setTextColor(
                ResourcesCompat.getColor(
                    textView.resources,
                    R.color.pink_d44b62,
                    null
                )
            )
            textView.text = RankEnum.A.rankName
        }
        RankEnum.B.name -> {
            textView.setTextColor(
                ResourcesCompat.getColor(
                    textView.resources,
                    R.color.blue_2b9bfd,
                    null
                )
            )
            textView.text = RankEnum.B.rankName
        }
        RankEnum.C.name -> {
            textView.setTextColor(
                ResourcesCompat.getColor(
                    textView.resources,
                    R.color.orange_ec7653,
                    null
                )
            )
            textView.text = RankEnum.C.rankName
        }
        RankEnum.D.name -> {
            textView.setTextColor(
                ResourcesCompat.getColor(
                    textView.resources,
                    R.color.dark_gray_818181,
                    null
                )
            )
            textView.text = RankEnum.D.rankName
        }
        RankEnum.NONE.name -> {
            textView.setTextColor(
                ResourcesCompat.getColor(
                    textView.resources,
                    R.color.medium_gray,
                    null
                )
            )
            textView.text = RankEnum.NONE.rankName
        }
    }
}

