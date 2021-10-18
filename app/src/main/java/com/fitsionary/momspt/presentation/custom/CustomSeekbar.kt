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
        textSize = 32F
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
            paint.color = ResourcesCompat.getColor(resources, R.color.light_gray_f9f9f9, null)
            val rect = RectF(
                thumbX - textBounds.exactCenterX() - 13,
                height / 4 + textBounds.exactCenterY() - textBounds.height() - 5,
                thumbX + textBounds.exactCenterX() + 13,
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
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.flag, null)
        drawable?.let {
            it.setBounds(
                width - 100,
                (height / 4 + textBounds.exactCenterY() + 10).toInt(),
                width,
                thumb.bounds.bottom
            )
            it.draw(canvas)
        }
    }

    fun setDay(day: Int) {
        this.day = day.toString()
        invalidate()
    }
}