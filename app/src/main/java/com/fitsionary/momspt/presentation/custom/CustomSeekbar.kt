package com.fitsionary.momspt.presentation.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.fitsionary.momspt.R


class CustomSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatSeekBar(context, attrs, defStyleAttr) {

    private var currentText: String? = null
    private val textBounds = Rect()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 30F
        color = ResourcesCompat.getColor(resources, R.color.white, null)
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
        typeface = Typeface.DEFAULT
    }

    @SuppressLint("DrawAllocation", "ResourceAsColor")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.flag, null)
        drawable?.let {
            it.setBounds(
                width - height / 2 - 10,
                0,
                width,
                height / 2 + 10
            )
            it.draw(canvas)
        }
        if (progress in 1 until max) {
            val thumbX = (progress.toFloat() / max * width.toFloat())
            currentText = "${progress}단계 3일차"
            currentText?.let {
                textPaint.getTextBounds(it, 0, it.length, textBounds)
            }
            val paint = Paint()
            paint.color = ResourcesCompat.getColor(resources, R.color.pink_99ec5363, null)
            val rect = RectF(
                thumbX - textBounds.exactCenterX() - 10,
                height / 4 + textBounds.exactCenterY() - textBounds.height() - 5,
                thumbX + textBounds.exactCenterX() + 10,
                height / 4 + textBounds.exactCenterY() + 10
            )
            canvas.drawRoundRect(rect, 30F, 30F, paint)
            canvas.drawText(
                currentText!!,
                thumbX,
                height / 4 + textBounds.exactCenterY(),
                textPaint
            )
        }
    }
}