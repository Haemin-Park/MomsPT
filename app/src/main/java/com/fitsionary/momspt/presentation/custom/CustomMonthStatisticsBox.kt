package com.fitsionary.momspt.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.databinding.DataBindingUtil
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.CustomMonthStatisticsBoxBinding

class CustomMonthStatisticsBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    val binding: CustomMonthStatisticsBoxBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.custom_month_statistics_box, this, true
    )

    init {
        context.withStyledAttributes(attrs, R.styleable.MonthStatisticsBox) {
            binding.tvCustomStatisticsBoxHeader.text =
                getString(R.styleable.MonthStatisticsBox_headerText)
            binding.tvCustomStatisticsBoxFooter.text =
                getString(R.styleable.MonthStatisticsBox_footerText)
        }
    }

    fun setBodyText(text: String) {
        binding.tvCustomStatisticsBoxBody.text = text
    }
}