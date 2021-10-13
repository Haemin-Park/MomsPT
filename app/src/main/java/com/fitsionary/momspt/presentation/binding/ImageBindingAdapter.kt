package com.fitsionary.momspt.presentation.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.fitsionary.momspt.R

@BindingAdapter("image_url")
fun setImageFromImageUrl(imageView: ImageView, imageUrl: String) {
    Glide.with(imageView.context)
        .load(imageUrl)
        .placeholder(R.drawable.placeholder)
        .into(imageView)
}

@BindingAdapter("round_image_url")
fun setRoundImageFromImageUrl(imageView: ImageView, imageUrl: String) {
    Glide.with(imageView.context)
        .load(imageUrl)
        .transform(CenterCrop(), RoundedCorners(60))
        .placeholder(R.drawable.placeholder)
        .into(imageView)
}

@BindingAdapter("android:src")
fun setImageFromResource(imageView: ImageView, resource: Int) {
    Glide.with(imageView.context)
        .load(resource)
        .into(imageView)
}

@BindingAdapter("circle_image_url")
fun setCircleImageFromImageUrl(imageView: ImageView, imageUrl: String) {
    Glide.with(imageView.context)
        .load(imageUrl)
        .circleCrop()
        .placeholder(R.drawable.placeholder)
        .into(imageView)
}

@BindingAdapter("score_image")
fun setScoreImage(imageView: ImageView, score: Int) {
    when (score) {
        in 1..29 -> {
            imageView.setImageResource(R.drawable.ic_bad)
        }
        in 30..59 -> {
            imageView.setImageResource(R.drawable.ic_cool)
        }
        in 60..89 -> {
            imageView.setImageResource(R.drawable.ic_great)
        }
        in 90..100 -> {
            imageView.setImageResource(R.drawable.ic_perpect)
        }
    }
}