package com.fitsionary.momspt.presentation.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.fitsionary.momspt.R


@SuppressLint("ClickableViewAccessibility")
class CustomSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatSeekBar(context, attrs, defStyleAttr) {

    private var currentText: String? = null
    private val textBounds = Rect()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 27F
        color = ResourcesCompat.getColor(resources, R.color.pink, null)
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
        typeface = Typeface.DEFAULT
    }
    private var day = ""

    init {
        this.setOnTouchListener { _, _ -> true }
    }

    @SuppressLint("DrawAllocation", "ResourceAsColor")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (progress in 1 until max) {
            val thumbX = (progress.toFloat() / max * width.toFloat())
            currentText = "${progress}단계 ${day}일차"
            currentText?.let {
                textPaint.getTextBounds(it, 0, it.length, textBounds)
            }
            val paint = Paint()
            paint.color = ResourcesCompat.getColor(resources, R.color.white, null)

            val bottom = thumb.bounds.bottom.toFloat() - 10
            val top = bottom - textBounds.height() - 10

            val rect = RectF(
                thumbX - textBounds.exactCenterX() - 13,
                top,
                thumbX + textBounds.exactCenterX() + 13,
                bottom
            )
            canvas.drawRoundRect(rect, 30F, 30F, paint)
            canvas.drawText(
                currentText!!,
                thumbX,
                bottom - (bottom - top) / 2 - textBounds.exactCenterY(),
                textPaint
            )
        }
    }

    fun setDay(day: Int) {
        this.day = day.toString()
        invalidate()
    }
}