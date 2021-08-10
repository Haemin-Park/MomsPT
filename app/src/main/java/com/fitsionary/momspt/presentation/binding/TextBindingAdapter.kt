package com.fitsionary.momspt.presentation.binding

import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.fitsionary.momspt.R

@BindingAdapter("score_gradient")
fun setScoreGradient(textView: TextView, score: Int) {
    val paint = textView.paint
    val width = paint.measureText(textView.text.toString())

    when (score) {
        in 0..59 -> {
            textView.setTextColor(textView.resources.getColor(R.color.gradient_blue))
            textView.paint.shader = null
        }
        in 60..89 -> {
            textView.setTextColor(textView.resources.getColor(R.color.gradient_pink))
            textView.paint.shader = null
        }
        else -> {
            val textShader = LinearGradient(
                0f, 0f, width, textView.textSize, intArrayOf(
                    textView.resources.getColor(R.color.gradient_pink),
                    textView.resources.getColor(R.color.gradient_blue),
                ), null, Shader.TileMode.CLAMP
            )
            textView.paint.shader = textShader
        }
    }
}

@BindingAdapter("score_comment")
fun setScoreComment(textView: TextView, score: Int) {
    val comment = when (score) {
        in 0..59 -> {
            "Bad"
        }
        in 60..89 -> {
            "Great!"
        }
        else -> {
            "Perfect!"
        }
    }
    textView.text = comment
}